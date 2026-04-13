package com.kofta.app.cache;

import com.kofta.app.events.PodContext;
import com.kofta.app.events.PodEvent;

public record EventCacheKey(
        Class<? extends PodEvent> eventClass, PodContext context, String specificState) {}
