package com.kofta.app.events;

import io.fabric8.kubernetes.api.model.Event;
import java.util.Optional;

public class EventRouter {

    public static PodEvent route(Event event) {
        switch (event.getReason()) {
            case "Pulled":
            case "Pulling":
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
            case "FailedScheduling": {
                // TODO: FIX
                return new SchedulingEvent(
                    PodContext.fromEvent(event),
                    event.getMessage(),
                    Optional.of("Unknown"),
                    true
                );
            }
            // ...

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
