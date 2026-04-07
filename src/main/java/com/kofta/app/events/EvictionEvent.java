package com.kofta.app.events;

public record EvictionEvent(PodContext context, String rawMessage, String reason) implements PodEvent {
}
