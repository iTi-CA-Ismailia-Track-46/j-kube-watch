package com.kofta.app.events;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.informers.cache.Lister;
import java.util.Optional;
import java.util.regex.Pattern;

public class PodEventFactory {

    private final Lister<Pod> podCache;

    public PodEventFactory(Lister<Pod> podCache) {
        this.podCache = podCache;
    }

    public ImageEvent createImageEvent(Event event) {
        return new ImageEvent(
            PodContext.fromEvent(event),
            event.getMessage(),
            extractImageName(event),
            ImageStatus.fromString(event.getReason())
        );
    }

    public SchedulingEvent createSchedulingEvent(Event event) {
        var nodeName = extractNodeName(event);
        return new SchedulingEvent(
            PodContext.fromEvent(event),
            event.getMessage(),
            nodeName,
            !event.getReason().toUpperCase().equals("FAILEDSCHEDULING") &&
                nodeName.isPresent()
        );
    }

    public LifecycleEvent createLifecycleEvent(Event event) {
        return new LifecycleEvent(
            PodContext.fromEvent(event),
            event.getMessage(),
            getContainer(event).get().getName(),
            LifecycleEventStatus.fromString(event.getReason())
        );
    }

    public ProbeFailureEvent createProbeFailureEvent(Event event) {
        return new ProbeFailureEvent(
            PodContext.fromEvent(event),
            event.getMessage(),
            getContainer(event).get().getName(),
            extractProbeType(event)
        );
    }

    public VolumeEvent createVolumeEvent(Event event) {
        String volumeName = extractVolumeName(event);
        return new VolumeEvent(
            PodContext.fromEvent(event),
            event.getMessage(),
            volumeName,
            !event.getReason().toUpperCase().equals("FAILEDMOUNT"),
            extractVolumeType(event, volumeName)
        );
    }

    public EvictionEvent createEvictionEvent(Event event) {
        return new EvictionEvent(
            PodContext.fromEvent(event),
            event.getMessage(),
            event.getReason()
        );
    }

    private String extractVolumeName(Event event) {
        var msg = event.getMessage();
        if (msg == null) return "Unknown";
        var pattern = Pattern.compile("volume \"(.+?)\"");
        var matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown";
    }

    private VolumeType extractVolumeType(Event event, String volumeName) {
        if ("Unknown".equals(volumeName)) return null;
        var pod = getCachedPod(event);
        if (
            pod == null ||
            pod.getSpec() == null ||
            pod.getSpec().getVolumes() == null
        ) return null;

        for (var volume : pod.getSpec().getVolumes()) {
            if (volume.getName().equals(volumeName)) {
                if (volume.getConfigMap() != null) return VolumeType.CONFIG_MAP;
                if (volume.getSecret() != null) return VolumeType.SECRET;
                if (
                    volume.getPersistentVolumeClaim() != null
                ) return VolumeType.PVC;
                if (volume.getEmptyDir() != null) return VolumeType.EMPTY_DIR;
                if (volume.getHostPath() != null) return VolumeType.HOST_PATH;
            }
        }
        return null;
    }

    private ProbeType extractProbeType(Event event) {
        var msg = event.getMessage();
        if (msg.startsWith("Liveness")) {
            return ProbeType.LIVENESS;
        } else if (msg.startsWith("Readiness")) {
            return ProbeType.READINESS;
        } else if (msg.startsWith("Startup")) {
            return ProbeType.STARTUP;
        } else {
            return null;
        }
    }

    private Optional<String> extractNodeName(Event event) {
        var pod = getCachedPod(event);
        return Optional.ofNullable(pod.getSpec().getNodeName());
    }

    private Optional<Container> getContainer(Event event) {
        var pod = getCachedPod(event);
        var fieldPath = event.getInvolvedObject().getFieldPath();
        var pattern = Pattern.compile("spec\\.containers\\{(.+?)\\}");
        var matcher = pattern.matcher(fieldPath);

        if (fieldPath != null && matcher.find()) {
            for (var container : pod.getSpec().getContainers()) {
                if (container.getName().equals(matcher.group(1))) {
                    return Optional.of(container);
                }
            }
        }

        return Optional.empty();
    }

    private String extractImageName(Event event) {
        var container = getContainer(event);

        if (container.isPresent()) {
            return container.get().getImage();
        }

        return null;
    }

    private Pod getCachedPod(Event event) {
        var obj = event.getInvolvedObject();
        return podCache.namespace(obj.getNamespace()).get(obj.getName());
    }
}
