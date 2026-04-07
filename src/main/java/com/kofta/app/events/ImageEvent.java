package com.kofta.app.events;

public record ImageEvent(PodContext context, String rawMessage, String imageName, ImageStatus imageStatus)
        implements PodEvent {
}

enum ImageStatus {
    PULLED,
    PULLING,
    FAILED
}