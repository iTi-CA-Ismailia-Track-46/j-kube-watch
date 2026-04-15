package com.jkube.app.cache;

import com.jkube.app.events.EvictionEvent;
import com.jkube.app.events.ImageEvent;
import com.jkube.app.events.LifecycleEvent;
import com.jkube.app.events.PodEvent;
import com.jkube.app.events.ProbeFailureEvent;
import com.jkube.app.events.SchedulingEvent;
import com.jkube.app.events.VolumeEvent;

public class CacheKeyExtractor {

    public static EventCacheKey extract(PodEvent event) {
        String specificState =
                switch (event) {
                    case SchedulingEvent e -> e.isSuccessful() ? "SCHEDULED" : "FAILED";
                    case ImageEvent e -> e.imageStatus().toString();
                    case LifecycleEvent e -> e.status().toString();
                    case ProbeFailureEvent e -> e.probeType().toString();
                    case VolumeEvent e ->
                            e.volumeName()
                                    + "-"
                                    + e.volumeType()
                                    + "-"
                                    + (e.isMounted() ? "MOUNTED" : "NOT_MOUNTED");
                    case EvictionEvent e -> e.reason();
                };

        return new EventCacheKey(event.getClass(), event.context(), specificState);
    }
}
