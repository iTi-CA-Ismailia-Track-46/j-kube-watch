package com.kofta.app.entities;

public class Pod {

    String name;
    String namespace;
    int restartCount;
    PodStatus podStatus;
}

enum PodStatus {
    Pending,
    Running,
    Succeeded,
    Failed,
    Unknown,
}
