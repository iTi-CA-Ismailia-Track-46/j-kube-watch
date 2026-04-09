package com.kofta.app.events;

public enum LifecycleEventStatus {
    STARTED,
    STOPPED,
    RESTARTED,
    CREATED,
    DELETED;

    public static LifecycleEventStatus fromString(String input) {
        return LifecycleEventStatus.valueOf(input.trim().toUpperCase());
    }
}