package com.kofta.app.events;

public record VolumeEvent(PodContext context, String rawMessage, String volumeName, boolean isMounted, VolumeType volumeType) implements PodEvent {
    
}