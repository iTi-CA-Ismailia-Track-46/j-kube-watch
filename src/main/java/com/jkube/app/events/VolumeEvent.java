package com.jkube.app.events;

import java.time.ZonedDateTime;

public record VolumeEvent(
        PodContext context,
        String rawMessage,
        String volumeName,
        boolean isMounted,
        VolumeType volumeType,
        String type,
        ZonedDateTime timestamp)
        implements PodEvent {}
