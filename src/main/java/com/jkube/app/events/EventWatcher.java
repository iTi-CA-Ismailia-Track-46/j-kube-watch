package com.jkube.app.events;

import com.jkube.app.cache.DeduplicationEngine;
import com.jkube.app.dispatchers.AlertDispatcher;
import com.jkube.app.utils.TimeStampParser;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;

public class EventWatcher implements Watcher<Event> {

    private final EventRouter router;
    private final AlertDispatcher<PodEvent> dispatcher;
    private final DeduplicationEngine deduplicationEngine;
    private final ExecutorService executor;
    private static final ZonedDateTime APP_START_TIME = ZonedDateTime.now(ZoneId.of("UTC"));

    public EventWatcher(
            EventRouter router,
            AlertDispatcher<PodEvent> dispatcher,
            DeduplicationEngine deduplicationEngine,
            ExecutorService executor) {
        this.router = router;
        this.dispatcher = dispatcher;
        this.deduplicationEngine = deduplicationEngine;
        this.executor = executor;
    }

    @Override
    public void eventReceived(Action action, Event event) {
        if (TimeStampParser.parseTimestamp(event.getLastTimestamp()).isBefore(APP_START_TIME)) {
            return;
        }

        PodEvent podEvent = router.route(event);
        if (podEvent == null) return;

        if (deduplicationEngine.tryRegister(podEvent)) {
            executor.submit(() -> dispatcher.dispatch(podEvent));
        } else {
            System.out.println("Suppressed duplicate event for " + podEvent.context().name());
        }
    }

    @Override
    public void onClose(WatcherException cause) {
        if (cause != null) {
            System.out.println("Watcher closed with error: " + cause.getMessage());
        }
    }
}
