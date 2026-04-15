package com.kofta.app.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.kofta.app.dispatchers.AlertDispatcher;
import com.kofta.app.events.PodEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DeduplicationEngine {

    private final Cache<EventCacheKey, EventSummary> cache;
    private static final int EXPIRATION_MINUTES = 2;
    private final AlertDispatcher<PodEvent> dispatcher;
    private final ExecutorService executor;

    public DeduplicationEngine(AlertDispatcher<PodEvent> dispatcher, ExecutorService executor) {
        this.cache =
                Caffeine.newBuilder()
                        .expireAfterWrite(EXPIRATION_MINUTES, TimeUnit.MINUTES)
                        .removalListener(this::onCacheExpiry)
                        .build();
        this.dispatcher = dispatcher;
        this.executor = executor;
    }

    /**
     * Attempts to register an event in the deduplication cache. If the event is a duplicate within
     * the time window, its internal counter is incremented and the registration is rejected.
     *
     * @param incomingEvent The Kubernetes event to process.
     * @return true if it is a new event that should be dispatched; false if it was suppressed as a
     *     duplicate.
     */
    public boolean tryRegister(PodEvent incomingEvent) {
        EventCacheKey key = CacheKeyExtractor.extract(incomingEvent);
        EventSummary existingSummary = cache.getIfPresent(key);

        if (existingSummary == null) {
            cache.put(key, new EventSummary(incomingEvent));
            return true; // Successfully added
        } else {
            existingSummary.increment();
            return false; // Rejected as duplicate
        }
    }

    private void onCacheExpiry(EventCacheKey key, EventSummary summary, RemovalCause cause) {
        if (summary != null && summary.getDuplicateCount() > 1) {
            String msg =
                    String.format(
                            "Muted Alert Summary: Pod %s had %d additional %s events in the last %d minutes.",
                            key.context().name(),
                            summary.getDuplicateCount()
                                    - 1, // Minus 1 because we sent the first one
                            key.eventClass().getSimpleName(),
                            EXPIRATION_MINUTES);

            System.out.println("DISPATCHING SUMMARY: " + msg);
            // TODO: Should have summary count later
            executor.submit(() -> dispatcher.dispatch(summary.getEvent()));
        }
    }
}
