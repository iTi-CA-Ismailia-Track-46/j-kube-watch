package com.kofta.app.events;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

public class EventWatcher implements Watcher<Event> {

    EventRouter router;

    public EventWatcher(EventRouter router) {
        this.router = router;
    }

    @Override
    public void eventReceived(Action action, Event event) {
        PodEvent podEvent = router.route(event);
        System.out.println(podEvent);

        System.out.println("-------------------------------------------");
    }

    @Override
    public void onClose(WatcherException cause) {}
}
