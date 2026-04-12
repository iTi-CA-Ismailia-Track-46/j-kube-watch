package com.kofta.app.events;

import java.time.ZonedDateTime;

public record ProbeFailureEvent(
    PodContext context,
    String rawMessage,
    String containerName,
    ProbeType probeType,
    String type,
    ZonedDateTime timestamp
) implements PodEvent {}
