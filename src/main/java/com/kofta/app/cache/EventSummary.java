package com.kofta.app.cache;

import com.kofta.app.events.PodEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class EventSummary {

    private final PodEvent podEvent;
    private final AtomicInteger duplicateCount;

    public EventSummary(PodEvent podEvent) {
        this.podEvent = podEvent;
        this.duplicateCount = new AtomicInteger(1);
    }

    public void increment() {
        this.duplicateCount.getAndIncrement();
    }

    public PodEvent getEvent() {
        return this.podEvent;
    }

    public int getDuplicateCount() {
        return this.duplicateCount.get();
    }
}
