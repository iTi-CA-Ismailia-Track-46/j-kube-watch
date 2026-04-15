package com.jkube.app.cache;

import com.jkube.app.events.PodContext;
import com.jkube.app.events.PodEvent;

public record EventCacheKey(
        Class<? extends PodEvent> eventClass, PodContext context, String specificState) {}
