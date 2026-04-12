package com.kofta.app.events;

import java.time.ZonedDateTime;
import java.util.Optional;

public record SchedulingEvent(
    PodContext context,
    String rawMessage,
    Optional<String> targetNode,
    boolean isSuccessful,
    String type,
    ZonedDateTime timestamp
) implements PodEvent {}
