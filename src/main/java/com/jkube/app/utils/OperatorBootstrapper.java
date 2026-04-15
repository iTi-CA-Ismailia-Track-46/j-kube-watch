package com.jkube.app.utils;

import com.jkube.app.cache.DeduplicationEngine;
import com.jkube.app.controllers.ReceiverController;
import com.jkube.app.controllers.SenderController;
import com.jkube.app.crd.receiver.AlertReceiver;
import com.jkube.app.crd.sender.AlertSender;
import com.jkube.app.dispatchers.CompositeAlertDispatcher;
import com.jkube.app.dispatchers.DispatcherFactory;
import com.jkube.app.events.EventRouter;
import com.jkube.app.events.EventWatcher;
import com.jkube.app.events.PodEventFactory;
import com.jkube.app.registry.AlertRegistry;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.cache.Lister;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OperatorBootstrapper {

    private final KubernetesClient client;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public OperatorBootstrapper(KubernetesClient client) {
        this.client = client;
    }

    public void start() throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        AlertRegistry registry = new AlertRegistry();

        var senderInformer = client.resources(AlertSender.class).inAnyNamespace().inform();
        var receiverInformer = client.resources(AlertReceiver.class).inAnyNamespace().inform();
        var podInformer = client.pods().inAnyNamespace().inform();

        senderInformer.addEventHandler(new SenderController(client, registry));
        receiverInformer.addEventHandler(new ReceiverController(registry));

        CompositeAlertDispatcher dispatcher = DispatcherFactory.build(registry, true);
        Lister<Pod> podCache = new Lister<>(podInformer.getIndexer());
        EventRouter router = new EventRouter(new PodEventFactory(podCache));
        DeduplicationEngine engine = new DeduplicationEngine(dispatcher, executor);

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    System.out.println("[SYSTEM] Shutting down...");
                                    senderInformer.close();
                                    receiverInformer.close();
                                    podInformer.close();
                                    executor.shutdown();
                                    shutdownLatch.countDown();
                                }));

        senderInformer.start();
        receiverInformer.start();
        podInformer.start();

        waitForSync(senderInformer, receiverInformer, podInformer);
        System.out.println("[SYSTEM] Informers synced. Listening for events...");

        client.v1()
                .events()
                .inAnyNamespace()
                .watch(new EventWatcher(router, dispatcher, engine, executor));

        shutdownLatch.await();
    }

    private void waitForSync(SharedIndexInformer<?>... informers) throws InterruptedException {
        while (!Arrays.stream(informers).allMatch(SharedIndexInformer::hasSynced)) {
            Thread.sleep(500);
        }
    }
}
