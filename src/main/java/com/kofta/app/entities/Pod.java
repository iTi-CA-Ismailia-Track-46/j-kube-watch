package com.kofta.app.entities;

public class Pod {
    String name;
    String namespace;
    int restartcount;
    String image;
    PodStatus podStatus;
}

enum PodStatus {
    Pending,
    Running,
    Succeeded,
    Failed,
    Unknown,
}