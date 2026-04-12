package com.kofta.app.events;
import java.time.ZonedDateTime;
public record ImageEvent(
    PodContext context,
    String rawMessage,
    String imageName,
    ImageStatus imageStatus,
    String type,
    ZonedDateTime timestamp
) implements PodEvent {}

