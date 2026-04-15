package com.jkube.app.events;

public enum LifecycleEventStatus {
    STARTED,
    STOPPED,
    RELOAD,
    BACKOFF,
    RESTARTED,
    CREATED;

    public static LifecycleEventStatus fromString(String input) {
        return LifecycleEventStatus.valueOf(input.trim().toUpperCase());
    }
}
