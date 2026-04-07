package com.kofta.app.events;

public sealed interface PodEvent permits SchedulingEvent {
    PodContext context(); // Every event must return the core Pod info
    String rawMessage(); // The human-readable message from K8s
}
