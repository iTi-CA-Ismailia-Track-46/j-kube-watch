package com.kofta.app.cache;

import com.kofta.app.events.EvictionEvent;
import com.kofta.app.events.ImageEvent;
import com.kofta.app.events.LifecycleEvent;
import com.kofta.app.events.PodEvent;
import com.kofta.app.events.ProbeFailureEvent;
import com.kofta.app.events.SchedulingEvent;
import com.kofta.app.events.VolumeEvent;

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
