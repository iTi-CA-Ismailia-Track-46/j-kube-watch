package com.kofta.app;

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

public class App {

    public static void main(String[] args)
        throws IOException, InterruptedException {
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

        podInformer.start();

        client.v1().events().watch(new EventWatcher(router));
    }
}
