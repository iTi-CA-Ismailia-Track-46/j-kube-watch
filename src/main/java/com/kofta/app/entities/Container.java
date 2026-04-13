package com.kofta.app.entities;

public record Container(String image, ContainerStatus status, int restartCount) {}
