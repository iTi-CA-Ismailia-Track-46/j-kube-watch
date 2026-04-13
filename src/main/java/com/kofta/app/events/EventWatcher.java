package com.kofta.app.events;

import com.kofta.app.cache.CacheKeyExtractor;
import com.kofta.app.cache.DeduplicationEngine;
import com.kofta.app.cache.EventCacheKey;
import com.kofta.app.cache.EventSummary;
import com.kofta.app.dispatchers.AlertDispatcher;
import com.kofta.app.utils.TimeStampParser;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventWatcher implements Watcher<Event> {

    private final EventRouter router;
    private final AlertDispatcher<PodEvent> dispatcher;
    private final DeduplicationEngine deduplicationEngine;
    private static final ZonedDateTime APP_START_TIME = ZonedDateTime.now(ZoneId.of("UTC"));

    public EventWatcher(
            EventRouter router,
            AlertDispatcher<PodEvent> dispatcher,
            DeduplicationEngine deduplicationEngine) {
        this.router = router;
        this.dispatcher = dispatcher;
        this.deduplicationEngine = deduplicationEngine;
    }

    @Override
    public void eventReceived(Action action, Event event) {
        if (TimeStampParser.parseTimestamp(event.getLastTimestamp()).isBefore(APP_START_TIME)) {
            return;
        }

        PodEvent podEvent = router.route(event);

        if (podEvent == null) return;

        EventCacheKey key = CacheKeyExtractor.extract(podEvent);
        var cache = deduplicationEngine.getCache();
        EventSummary eventSummary = cache.getIfPresent(key);

        if (eventSummary == null) {
            cache.put(key, new EventSummary(podEvent));
            dispatcher.dispatch(podEvent);
        } else {
            eventSummary.increment();
            System.out.println("Suppressed duplicate event for " + key.context().name());
        }
    }

    @Override
    public void onClose(WatcherException cause) {
        if (cause != null) {
            System.out.println("Watcher closed with error: " + cause.getMessage());
        }
    }
}
