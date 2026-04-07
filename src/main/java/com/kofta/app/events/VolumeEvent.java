package com.kofta.app.events;

public record VolumeEvent(PodContext context, String rawMessage, String volumeName, boolean isMounted, VolumeType volumeType) implements PodEvent {
    
}

enum VolumeType {
    CONFIGMAP,
    SECRET,
    PERSISTENT_VOLUME_CLAIM,
    EMPTY_DIR
}