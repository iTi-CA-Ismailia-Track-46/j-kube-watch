package com.kofta.app.events;

import java.util.Optional;

public record SchedulingEvent(
    PodContext context,
    String rawMessage,
    Optional<String> targetNode,
    boolean isSuccessful
) implements KubeEvent {}
