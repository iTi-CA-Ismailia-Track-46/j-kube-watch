package com.kofta.app.events;

import java.time.ZonedDateTime;

public sealed interface PodEvent
        permits SchedulingEvent,
                ImageEvent,
                LifecycleEvent,
                ProbeFailureEvent,
                VolumeEvent,
                EvictionEvent {
    PodContext context(); // Every event must return the core Pod info

    String rawMessage(); // The human-readable message from K8s

    String type(); // The type of event (Normal, Warning, etc.)

    ZonedDateTime timestamp(); // When the event occurred
}
