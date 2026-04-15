package com.jkube.app.events;

import io.fabric8.kubernetes.api.model.Event;

public record PodContext(String name, String namespace) {
    public static PodContext fromEvent(Event event) {
        return new PodContext(
                event.getInvolvedObject().getName(), event.getInvolvedObject().getNamespace());
    }
}
