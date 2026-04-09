package com.kofta.app.events;

public enum ImageStatus {
    PULLED,
    PULLING,
    BACKOFF,
    FAILED;

    public static ImageStatus fromString(String input) {
        return ImageStatus.valueOf(input.trim().toUpperCase());
    }
}
