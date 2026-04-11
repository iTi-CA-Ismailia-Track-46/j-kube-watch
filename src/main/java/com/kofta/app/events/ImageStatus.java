package com.kofta.app.events;

public enum ImageStatus {
    PULLED,
    PULLING,
    FAILED;

    public static ImageStatus fromString(String input) {
        return ImageStatus.valueOf(input.trim().toUpperCase());
    }
}
