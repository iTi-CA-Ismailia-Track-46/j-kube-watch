package com.kofta.app.events;

import java.time.ZonedDateTime;

public record LifecycleEvent(
        PodContext context,
        String rawMessage,
        String containerName,
        LifecycleEventStatus status,
        String type,
        ZonedDateTime timestamp)
        implements PodEvent {}
