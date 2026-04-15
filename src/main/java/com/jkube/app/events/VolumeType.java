package com.jkube.app.events;

public enum VolumeType {
    CONFIG_MAP,
    SECRET,
    PVC,
    EMPTY_DIR,
    HOST_PATH;

    public static VolumeType fromString(String input) {
        return VolumeType.valueOf(input.trim().toUpperCase());
    }
}
