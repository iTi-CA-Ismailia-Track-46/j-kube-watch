package com.kofta.app.entities;

public class Container {

    String image;
    ContainerStatus status;
    int restartCount;
}

enum ContainerStatus {
    Waiting,
    Running,
    Terminated,
}
