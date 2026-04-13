package com.kofta.app.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import java.util.concurrent.TimeUnit;

public class DeduplicationEngine {

    private final Cache<EventCacheKey, EventSummary> cache;
    private static final int EXPIRATION_MINUTES = 2;

    public DeduplicationEngine() {
        this.cache =
                Caffeine.newBuilder()
                        .expireAfterWrite(EXPIRATION_MINUTES, TimeUnit.MINUTES)
                        .removalListener(this::onCacheExpiry)
                        .build();
    }

    public Cache<EventCacheKey, EventSummary> getCache() {
        return this.cache;
    }

    private void onCacheExpiry(EventCacheKey key, EventSummary summary, RemovalCause cause) {
        if (summary != null && summary.getDuplicateCount() > 1) {
            String msg =
                    String.format(
                            "Muted Alert Summary: Pod %s had %d additional %s events in the last %i minutes.",
                            key.context().name(),
                            summary.getDuplicateCount()
                                    - 1, // Minus 1 because we sent the first one
                            key.eventClass().getSimpleName(),
                            EXPIRATION_MINUTES);

            System.out.println("DISPATCHING SUMMARY: " + msg);
            // dispatcher.dispatch(new SummaryEvent(msg));
        }
    }
}
