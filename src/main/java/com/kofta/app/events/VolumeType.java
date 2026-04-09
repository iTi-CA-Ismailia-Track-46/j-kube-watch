package com.kofta.app.events;

public enum VolumeType {
    CONFIGMAP,
    SECRET,
    PERSISTENT_VOLUME_CLAIM,
    EMPTY_DIR;

    public static VolumeType fromString(String input) {
        return VolumeType.valueOf(input.trim().toUpperCase());
    }
}