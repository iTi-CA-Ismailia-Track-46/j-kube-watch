package com.kofta.app.events;

import io.fabric8.kubernetes.api.model.Event;
import java.util.Optional;

public class EventRouter {

    public static PodEvent route(Event event) {
        switch (event.getReason()) {
            case "Pulled":
            case "Pulling":
            case "BackOff":
            case "Failed": {
                return new ImageEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    // TODO: FIX
                    "Unknown",
                    ImageStatus.fromString(event.getReason())
                );
            }
            case "Scheduling":
            case "Scheduled":
            case "FailedScheduling": {
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
            case "DELETED": {
                return new LifecycleEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    event.getInvolvedObject().getName(),
                    LifecycleEventStatus.fromString(event.getReason())
                );
            }
            case "LivenessProbeFailed":
            case "ReadinessProbeFailed": {
                return new ProbeFailureEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    event.getInvolvedObject().getName(),
                    event.getReason()
                );
            }
            case "CONFIGMAP":
            case "SECRET":      
            case "PERSISTENT_VOLUME_CLAIM":
            case "EMPTY_DIR": {
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
