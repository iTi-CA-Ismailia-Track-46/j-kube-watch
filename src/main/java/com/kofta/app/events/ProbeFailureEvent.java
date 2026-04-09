package com.kofta.app.events;

// String probeType (Did it fail the Liveness probe or the Readiness probe? The raw K8s event message usually contains this).
public record ProbeFailureEvent(PodContext context, String rawMessage, String containerName, String probeType)
        implements PodEvent {
}
