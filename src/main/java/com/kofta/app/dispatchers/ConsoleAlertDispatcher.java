package com.kofta.app.dispatchers;

import com.kofta.app.events.*;
import java.time.format.DateTimeFormatter;

public class ConsoleAlertDispatcher implements AlertDispatcher<PodEvent> {

    private static final DateTimeFormatter TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void dispatch(PodEvent event) {
        System.out.println(
            "------------------------------------------------------"
        );

        String podName = "Unknown";
        String namespace = "Unknown";

        switch (event) {
            case SchedulingEvent e -> {
                podName = e.context().name();
                namespace = e.context().namespace();
                String status = e.isSuccessful() ? "SUCCESS" : "FAILED";
                System.out.println("[EVENT]      : SCHEDULING");
                System.out.println("[SEVERITY]   : " + e.type());
                System.out.println(
                    "[TIME]       : " + e.timestamp().format(TIME_FORMATTER)
                );
                System.out.println("[STATUS]     : " + status);
                System.out.println(
                    "[NODE]       : " + e.targetNode().orElse("Pending...")
                );
                System.out.println("[MESSAGE]    : " + e.rawMessage());
            }
            case ImageEvent e -> {
                podName = e.context().name();
                namespace = e.context().namespace();
                System.out.println("[EVENT]      : IMAGE");
                System.out.println("[SEVERITY]   : " + e.type());
                System.out.println(
                    "[TIME]       : " + e.timestamp().format(TIME_FORMATTER)
                );
                System.out.println("[STATUS]     : " + e.imageStatus());
                System.out.println("[IMAGE]      : " + e.imageName());
                System.out.println("[MESSAGE]    : " + e.rawMessage());
            }
            case LifecycleEvent e -> {
                podName = e.context().name();
                namespace = e.context().namespace();
                System.out.println("[EVENT]      : LIFECYCLE");
                System.out.println("[SEVERITY]   : " + e.type());
                System.out.println(
                    "[TIME]       : " + e.timestamp().format(TIME_FORMATTER)
                );
                System.out.println("[STATUS]     : " + e.status());
                System.out.println("[CONTAINER]  : " + e.containerName());
                System.out.println("[MESSAGE]    : " + e.rawMessage());
            }
            case ProbeFailureEvent e -> {
                podName = e.context().name();
                namespace = e.context().namespace();
                System.out.println("[EVENT]      : PROBE FAILURE");
                System.out.println("[SEVERITY]   : " + e.type());
                System.out.println(
                    "[TIME]       : " + e.timestamp().format(TIME_FORMATTER)
                );
                System.out.println("[TYPE]       : " + e.probeType());
                System.out.println("[CONTAINER]  : " + e.containerName());
                System.out.println("[MESSAGE]    : " + e.rawMessage());
            }
            case VolumeEvent e -> {
                podName = e.context().name();
                namespace = e.context().namespace();
                String status = e.isMounted() ? "MOUNTED" : "FAILED";
                System.out.println("[EVENT]      : VOLUME");
                System.out.println("[SEVERITY]   : " + e.type());
                System.out.println(
                    "[TIME]       : " + e.timestamp().format(TIME_FORMATTER)
                );
                System.out.println("[STATUS]     : " + status);
                System.out.println(
                    "[VOLUME]     : " +
                        e.volumeName() +
                        " (" +
                        e.volumeType() +
                        ")"
                );
                System.out.println("[MESSAGE]    : " + e.rawMessage());
            }
            case EvictionEvent e -> {
                podName = e.context().name();
                namespace = e.context().namespace();
                System.out.println("[EVENT]      : EVICTION");
                System.out.println("[SEVERITY]   : " + e.type());
                System.out.println(
                    "[TIME]       : " + e.timestamp().format(TIME_FORMATTER)
                );
                System.out.println("[REASON]     : " + e.reason());
                System.out.println("[MESSAGE]    : " + e.rawMessage());
            }
            default -> {
                System.out.println("[EVENT]      : UNKNOWN");
                System.out.println("[DATA]       : " + event.toString());
            }
        }

        System.out.println(
            "[TARGET]     : " + podName + " (Namespace: " + namespace + ")"
        );
        System.out.println(
            "------------------------------------------------------\n"
        );
    }
}
