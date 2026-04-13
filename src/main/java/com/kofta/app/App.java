package com.kofta.app;

import com.kofta.app.utils.OperatorBootstrapper;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

public class App {

    public static void main(String[] args) throws InterruptedException {
        KubernetesClient client = new KubernetesClientBuilder().build();
        new OperatorBootstrapper(client).start();
    }
}
