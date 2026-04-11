package com.kofta.app.events;

import io.fabric8.kubernetes.api.model.Event;

public class EventRouter {

    private final PodEventFactory eventFactory;

    public EventRouter(PodEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public PodEvent route(Event event) {
        switch (event.getReason().toUpperCase()) {
            case "PULLED":
            case "PULLING":
            case "FAILED": {
                return eventFactory.createImageEvent(event);
            }
            case "SCHEDULING":
            case "SCHEDULED":
            case "FAILEDSCHEDULING": {
                return eventFactory.createSchedulingEvent(event);
            }
            case "STARTED":
            case "STOPPED":
            case "RELOAD":
            case "RESTARTED":
            case "CREATED":
            case "BACKOFF":
            case "KILLING": {
                return eventFactory.createLifecycleEvent(event);
            }
            case "UNHEALTHY": {
                return eventFactory.createProbeFailureEvent(event);
            }
            case "FAILEDMOUNT":
            case "SUCCESSFULATTACHVOLUME": {
                return eventFactory.createVolumeEvent(event);
            }
            case "EVICTED":
            case "PREEMPTING": {
                return eventFactory.createEvictionEvent(event);
            }
            default: {
                return null;
            }
        }
    }
}
