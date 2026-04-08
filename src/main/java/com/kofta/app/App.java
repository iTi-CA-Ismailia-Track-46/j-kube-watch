package com.kofta.app;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws IOException {
        var content = Files.readString(Path.of("/home/kofta/.kube/config"));
        var config = Config.fromKubeconfig(content);

        KubernetesClient client = new KubernetesClientBuilder()
            .withConfig(config)
            .build();

        client
            .pods()
            .inNamespace("default")
            .watch(
                new Watcher<Pod>() {
                    @Override
                    public void eventReceived(Action action, Pod resource) {
                        System.out.print(action + " ");
                        System.out.print(
                            resource.getMetadata().getName() + " "
                        );
                        System.out.println(
                            resource.getStatus().getConditions()
                        );
                    }

                    @Override
                    public void onClose(WatcherException cause) {
                        // TODO Auto-generated method stub
                        throw new UnsupportedOperationException(
                            "Unimplemented method 'onClose'"
                        );
                    }
                }
            );
    }
}
