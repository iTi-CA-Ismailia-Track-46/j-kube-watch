package com.kofta.app;

import com.kofta.app.events.EventRouter;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
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

        client.v1().events().watch(new Events());
    }
}

class Events implements Watcher<Event> {

    @Override
    public void eventReceived(Action action, Event event) {
        System.out.println(EventRouter.route(event));

        // System.out.print(resource.getMessage() + " ");
        // System.out.print(resource.getInvolvedObject().getKind() + " ");
        // System.out.print(resource.getInvolvedObject().getName() + " ");
        // System.out.print(resource.getReason() + " ");
        // System.out.print(resource.getEventTime() + " ");
        // System.out.println(resource.getMetadata().getNamespace() + " ");
        // System.out.println(resource.getMessage());
        System.out.println("*******************************************");
    }

    @Override
    public void onClose(WatcherException cause) {}
}
