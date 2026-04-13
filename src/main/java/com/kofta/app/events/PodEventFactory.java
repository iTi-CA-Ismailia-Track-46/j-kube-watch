package com.kofta.app.events;

import com.kofta.app.utils.TimeStampParser;

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
                ImageStatus.fromString(event.getReason()),
                event.getType(),
                TimeStampParser.parseTimestamp(event.getLastTimestamp()));
    }

    public SchedulingEvent createSchedulingEvent(Event event) {
        var nodeName = extractNodeName(event);
        return new SchedulingEvent(
                PodContext.fromEvent(event),
                event.getMessage(),
                nodeName,
                !event.getReason().equalsIgnoreCase("FAILEDSCHEDULING") && nodeName.isPresent(),
                event.getType(),
                TimeStampParser.parseTimestamp(event.getLastTimestamp()));
    }

    public LifecycleEvent createLifecycleEvent(Event event) {
        Optional<Container> container = getContainer(event);
        if (container.isEmpty()) return null;

        return new LifecycleEvent(
                PodContext.fromEvent(event),
                event.getMessage(),
                container.get().getName(),
                LifecycleEventStatus.fromString(event.getReason()),
                event.getType(),
                TimeStampParser.parseTimestamp(event.getLastTimestamp()));
    }

    public ProbeFailureEvent createProbeFailureEvent(Event event) {
        Optional<Container> container = getContainer(event);
        if (container.isEmpty()) return null;

        return new ProbeFailureEvent(
                PodContext.fromEvent(event),
                event.getMessage(),
                container.get().getName(),
                extractProbeType(event),
                event.getType(),
                TimeStampParser.parseTimestamp(event.getLastTimestamp()));
    }

    public VolumeEvent createVolumeEvent(Event event) {
        String volumeName = extractVolumeName(event);
        return new VolumeEvent(
                PodContext.fromEvent(event),
                event.getMessage(),
                volumeName,
                !event.getReason().equalsIgnoreCase("FAILEDMOUNT"),
                extractVolumeType(event, volumeName),
                event.getType(),
                TimeStampParser.parseTimestamp(event.getLastTimestamp()));
    }

    public EvictionEvent createEvictionEvent(Event event) {
        return new EvictionEvent(
                PodContext.fromEvent(event),
                event.getMessage(),
                event.getReason(),
                event.getType(),
                TimeStampParser.parseTimestamp(event.getLastTimestamp()));
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
        if (pod == null || pod.getSpec() == null || pod.getSpec().getVolumes() == null) return null;

        for (var volume : pod.getSpec().getVolumes()) {
            if (volume.getName().equals(volumeName)) {
                if (volume.getConfigMap() != null) return VolumeType.CONFIG_MAP;
                if (volume.getSecret() != null) return VolumeType.SECRET;
                if (volume.getPersistentVolumeClaim() != null) return VolumeType.PVC;
                if (volume.getEmptyDir() != null) return VolumeType.EMPTY_DIR;
                if (volume.getHostPath() != null) return VolumeType.HOST_PATH;
            }
        }
        return null;
    }

    private ProbeType extractProbeType(Event event) {
        var msg = event.getMessage();
        if (msg == null) return null;
        if (msg.startsWith("Liveness")) return ProbeType.LIVENESS;
        if (msg.startsWith("Readiness")) return ProbeType.READINESS;
        if (msg.startsWith("Startup")) return ProbeType.STARTUP;
        return null;
    }

    private Optional<String> extractNodeName(Event event) {
        var pod = getCachedPod(event);
        if (pod == null || pod.getSpec() == null) return Optional.empty();
        return Optional.ofNullable(pod.getSpec().getNodeName());
    }

    private Optional<Container> getContainer(Event event) {
        var pod = getCachedPod(event);
        var fieldPath = event.getInvolvedObject().getFieldPath();
        if (pod == null || pod.getSpec() == null || fieldPath == null) return Optional.empty();

        var pattern = Pattern.compile("spec\\.containers\\{(.+?)\\}");
        var matcher = pattern.matcher(fieldPath);

        if (matcher.find()) {
            String containerName = matcher.group(1);
            for (var container : pod.getSpec().getContainers()) {
                if (container.getName().equals(containerName)) {
                    return Optional.of(container);
                }
            }
        }
        return Optional.empty();
    }

    private String extractImageName(Event event) {
        return getContainer(event).map(Container::getImage).orElse(null);
    }

    private Pod getCachedPod(Event event) {
        var obj = event.getInvolvedObject();
        if (obj == null) return null;
        return podCache.namespace(obj.getNamespace()).get(obj.getName());
    }
}
