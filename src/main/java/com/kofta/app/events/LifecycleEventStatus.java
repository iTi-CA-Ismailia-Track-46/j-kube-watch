package com.kofta.app.events;

public enum LifecycleEventStatus {
    STARTED,
    STOPPED,
    RELOAD,
    RESTARTED,
    CREATED,
    DELETED;

    public static LifecycleEventStatus fromString(String input) {
        return LifecycleEventStatus.valueOf(input.trim().toUpperCase());
    }
}