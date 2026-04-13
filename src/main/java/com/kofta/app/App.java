package com.kofta.app;



import com.kofta.app.controllers.ReceiverController;

import com.kofta.app.controllers.SenderController;

import com.kofta.app.crd.receiver.AlertReceiver;

import com.kofta.app.crd.sender.AlertSender;

import com.kofta.app.dispatchers.CompositeAlertDispatcher;

import com.kofta.app.dispatchers.ConsoleAlertDispatcher;

import com.kofta.app.dispatchers.EmailAlertDispatcher;

import com.kofta.app.events.EventRouter;

import com.kofta.app.events.EventWatcher;

import com.kofta.app.events.PodEventFactory;

import com.kofta.app.registry.AlertRegistry;

import io.fabric8.kubernetes.api.model.Pod;

import io.fabric8.kubernetes.client.KubernetesClient;

import io.fabric8.kubernetes.client.KubernetesClientBuilder;

import io.fabric8.kubernetes.client.informers.SharedIndexInformer;

import io.fabric8.kubernetes.client.informers.cache.Lister;

import java.util.List;



public class App {



    public static void main(String[] args) throws InterruptedException {

        KubernetesClient client = new KubernetesClientBuilder().build();

        AlertRegistry registry = new AlertRegistry();



        SharedIndexInformer<AlertSender> senderInformer = client.resources(AlertSender.class).inAnyNamespace().inform();

        senderInformer.addEventHandler(new SenderController(client, registry));



        SharedIndexInformer<AlertReceiver> receiverInformer = client.resources(AlertReceiver.class).inAnyNamespace().inform();

        receiverInformer.addEventHandler(new ReceiverController(registry));



        SharedIndexInformer<Pod> podInformer = client.pods().inAnyNamespace().inform();

        Lister<Pod> podCache = new Lister<>(podInformer.getIndexer());

        PodEventFactory podEventFactory = new PodEventFactory(podCache);

        EventRouter router = new EventRouter(podEventFactory);



        ConsoleAlertDispatcher consoleDispatcher = new ConsoleAlertDispatcher();

        EmailAlertDispatcher emailDispatcher = new EmailAlertDispatcher(registry);

        CompositeAlertDispatcher multiDispatcher = new CompositeAlertDispatcher(List.of(consoleDispatcher, emailDispatcher));



        System.out.println("[SYSTEM] Starting Informers...");

        senderInformer.start();

        receiverInformer.start();

        podInformer.start();



        while (!senderInformer.hasSynced() || !receiverInformer.hasSynced() || !podInformer.hasSynced()) {

            Thread.sleep(500);

        }

        System.out.println("[SYSTEM] Informers synced.");



        System.out.println("[SYSTEM] Listening for Kubernetes Events...");

        client.v1().events().inAnyNamespace().watch(new EventWatcher(router, multiDispatcher));



        Thread.sleep(Long.MAX_VALUE);

    }

}
