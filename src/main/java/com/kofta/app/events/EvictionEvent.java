package com.kofta.app.events;

import java.time.ZonedDateTime;

public record EvictionEvent(PodContext context, String rawMessage, String reason , String type , ZonedDateTime timestamp) implements PodEvent {
}
