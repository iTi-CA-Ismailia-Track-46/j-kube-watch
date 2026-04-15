package com.jkube.app.crd.receiver;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("j-kube-watch.app")
@Version("v1")
public class AlertReceiver extends CustomResource<AlertReceiverSpec, Void> implements Namespaced {}
