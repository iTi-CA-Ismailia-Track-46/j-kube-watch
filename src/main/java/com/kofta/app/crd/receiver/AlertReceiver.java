package com.kofta.app.crd.receiver;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Version;

@io.fabric8.kubernetes.model.annotation.Group("creative.app")
@Version("v1")
public class AlertReceiver extends CustomResource<AlertReceiverSpec, Void> implements Namespaced {
}