package com.kofta.app.events;

import io.fabric8.kubernetes.api.model.Event;
import java.util.Optional;

public class EventRouter {

    public static PodEvent route(Event event) {
        switch (event.getReason().toUpperCase()) {
            case "PULLED":
            case "PULLING":
            case "FAILED": {
                return new ImageEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    // TODO: FIX
                    "Unknown",
                    ImageStatus.fromString(event.getReason())
                );
            }
            case "SCHEDULING":
            case "SCHEDULED":
            case "FAILEDSCHEDULING": {
                // TODO: FIX
                return new SchedulingEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    Optional.of("Unknown"),
                    true
                );
            }
            case "STARTED":
            case "STOPPED":
            case "RELOAD":
            case "RESTARTED":
            case "CREATED":
            case "BACKOFF":
            case "KILLING": {
                return new LifecycleEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    event.getInvolvedObject().getName(),
                    LifecycleEventStatus.fromString(event.getReason())
                );
            }
            case "UNHEALTHY": {
                return new ProbeFailureEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    event.getInvolvedObject().getName(),
                    // TODO: FIX
                    event.getReason()
                );
            }
            case "SUCCESSFULATTACHVOLUME":
            case "FAILEDMOUNT": {
                return new VolumeEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    event.getInvolvedObject().getName(),
                    true, // TODO: FIX
                    VolumeType.fromString(event.getReason()) // TODO: FIX
                );
            }
            default: {
                return new EvictionEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    "PLACEHOLDER FOR OTHER EVENTS"
                );
            }
        }
    }
}
