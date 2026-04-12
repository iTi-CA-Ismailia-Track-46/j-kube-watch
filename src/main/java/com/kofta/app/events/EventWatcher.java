package com.kofta.app.events;

import com.kofta.app.dispatchers.AlertDispatcher;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

public class EventWatcher implements Watcher<Event> {

    private final EventRouter router;
    private final AlertDispatcher<PodEvent> dispatcher;

    public EventWatcher(EventRouter router, AlertDispatcher<PodEvent> dispatcher) {
        this.router = router;
        this.dispatcher = dispatcher;
    }

    @Override
    public void eventReceived(Action action, Event event) {
        PodEvent podEvent = router.route(event);
        
        if (podEvent != null) {
            dispatcher.dispatch(podEvent);
        }
    }

    @Override
    public void onClose(WatcherException cause) {
        if (cause != null) {
            System.out.println("Watcher closed with error: " + cause.getMessage());
        }
    }
}