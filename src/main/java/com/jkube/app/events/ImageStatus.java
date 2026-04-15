package com.jkube.app.events;

public enum ImageStatus {
    PULLED,
    PULLING,
    FAILED;

    public static ImageStatus fromString(String input) {
        return ImageStatus.valueOf(input.trim().toUpperCase());
    }
}
