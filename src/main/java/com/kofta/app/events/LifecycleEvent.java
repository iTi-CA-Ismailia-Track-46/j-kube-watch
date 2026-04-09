package com.kofta.app.events;

public record LifecycleEvent(PodContext context, String rawMessage, String containerName, LifecycleEventStatus status)
        implements PodEvent {
}
