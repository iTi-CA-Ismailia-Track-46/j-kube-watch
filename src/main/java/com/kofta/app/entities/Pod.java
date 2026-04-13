package com.kofta.app.entities;

public record Pod(
        String name,
        String namespace,
        int restartCount,
        PodStatus podStatus,
        Container[] containers) {}
