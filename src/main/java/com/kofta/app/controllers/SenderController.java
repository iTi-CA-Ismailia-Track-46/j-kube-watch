package com.kofta.app.controllers;


import com.kofta.app.crd.sender.AlertSender;
import com.kofta.app.registry.AlertRegistry;
import com.kofta.app.utils.SmtpValidator;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import javax.mail.Session;
import java.util.Base64;

public class SenderController implements ResourceEventHandler<AlertSender> {
    private final KubernetesClient client;
    private final AlertRegistry registry;

    public SenderController(KubernetesClient client, AlertRegistry registry) {
        this.client = client;
        this.registry = registry;
    }

    @Override
    public void onAdd(AlertSender sender) {
        process(sender);
    }

    @Override
    public void onUpdate(AlertSender oldSender, AlertSender newSender) {
        process(newSender);
    }

    @Override
    public void onDelete(AlertSender sender, boolean deletedFinalStateUnknown) {
        registry.removeSender(sender.getMetadata().getName());
    }

    private void process(AlertSender sender) {
        String name = sender.getMetadata().getName();
        String namespace = sender.getMetadata().getNamespace();
        String secretName = sender.getSpec().getSecretName();

        Secret secret = client.secrets().inNamespace(namespace).withName(secretName).get();
        if (secret == null || secret.getData() == null || !secret.getData().containsKey("password")) {
            System.err.println("[ERROR] Missing or invalid secret '" + secretName + "' for Sender: " + name);
            registry.removeSender(name);
            return;
        }

        String password = secret.getData().get("password");

        try {
            Session session = SmtpValidator.validateAndCreateSession(
                sender.getSpec().getHost(),
                sender.getSpec().getPort(),
                sender.getSpec().getEmail(),
                password
            );
            registry.addSender(name, sender.getSpec().getEmail(), session);
            System.out.println("[INFO] Sender authenticated successfully: " + name);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to authenticate Sender '" + name + "': " + e.getMessage());
            registry.removeSender(name);
        }
    }
}