package com.kofta.app.controllers;

import com.kofta.app.crd.receiver.AlertReceiver;
import com.kofta.app.registry.AlertRegistry;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;

public class ReceiverController implements ResourceEventHandler<AlertReceiver> {
    private final AlertRegistry registry;

    public ReceiverController(AlertRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onAdd(AlertReceiver receiver) {
        process(receiver);
    }

    @Override
    public void onUpdate(AlertReceiver oldReceiver, AlertReceiver newReceiver) {
        process(newReceiver);
    }

    @Override
    public void onDelete(AlertReceiver receiver, boolean deletedFinalStateUnknown) {
        registry.removeReceiver(receiver.getMetadata().getName());
    }

    private void process(AlertReceiver receiver) {
        String name = receiver.getMetadata().getName();
        String senderRef = receiver.getSpec().getSenderRef();

        if (!registry.hasSender(senderRef)) {
            System.err.println("[WARNING] Receiver '" + name + "' references an invalid or missing Sender: " + senderRef);
        }

        registry.addReceiver(name, receiver.getSpec().getEmail(), senderRef);
        System.out.println("[INFO] Receiver registered: " + name);
    }
}