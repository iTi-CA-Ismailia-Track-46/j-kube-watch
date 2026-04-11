package com.kofta.app.events;

public enum LifecycleEventStatus {
    STARTED,
    STOPPED,
    RELOAD,
    BACKOFF,
    RESTARTED,
    CREATED,
    KILLING;

    public static LifecycleEventStatus fromString(String input) {
        return LifecycleEventStatus.valueOf(input.trim().toUpperCase());
    }
}
