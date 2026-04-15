package com.jkube.app.crd.sender;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("j-kube-watch.app")
@Version("v1")
public class AlertSender extends CustomResource<AlertSenderSpec, Void> implements Namespaced {}
