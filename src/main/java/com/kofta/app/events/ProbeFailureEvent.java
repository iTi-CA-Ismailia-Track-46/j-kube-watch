package com.kofta.app.events;

public record ProbeFailureEvent(
    PodContext context,
    String rawMessage,
    String containerName,
    ProbeType probeType
) implements PodEvent {}
