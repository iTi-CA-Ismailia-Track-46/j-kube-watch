package com.kofta.app;

import com.kofta.app.dispatchers.CompositeAlertDispatcher;
import com.kofta.app.dispatchers.ConsoleAlertDispatcher;
import com.kofta.app.dispatchers.EmailAlertDispatcher;
import com.kofta.app.events.EventRouter;
import com.kofta.app.events.EventWatcher;
import com.kofta.app.events.PodEventFactory;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.cache.Lister;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        var dotenv = Dotenv.load();
        var configPath = dotenv.get("CONFIG_FILE_PATH");
        var content = Files.readString(Path.of(configPath));
        var config = Config.fromKubeconfig(content);

        KubernetesClient client = new KubernetesClientBuilder()
            .withConfig(config)
            .build();

        SharedIndexInformer<Pod> podInformer = client.pods().inform();
        Lister<Pod> podCache = new Lister<>(podInformer.getIndexer());
        var podEventFactory = new PodEventFactory(podCache);
        var router = new EventRouter(podEventFactory);

        var consoleDispatcher = new ConsoleAlertDispatcher();
        
        var emailDispatcher = new EmailAlertDispatcher(
            dotenv.get("SMTP_HOST"),
            dotenv.get("SMTP_PORT"),
            dotenv.get("SMTP_USER"),
            dotenv.get("SMTP_PASS"),
            dotenv.get("ALERT_RECEIVER_EMAIL")
        );

        var multiDispatcher = new CompositeAlertDispatcher(
            List.of(consoleDispatcher, emailDispatcher)
        );

        System.out.println("Starting Pod Informer...");
        podInformer.start();

        System.out.println("Listening for Kubernetes Events...");
        client.v1().events().watch(new EventWatcher(router, multiDispatcher));
        
        Thread.sleep(Long.MAX_VALUE);
    }
}