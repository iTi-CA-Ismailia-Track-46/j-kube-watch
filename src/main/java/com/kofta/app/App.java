package com.kofta.app;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import java.io.IOException;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws IOException, ApiException {
        ApiClient apiClient = Config.defaultClient();
        Configuration.setDefaultApiClient(apiClient);

        var api = new CoreV1Api();

        var nodes = api
            .listNode()
            .labelSelector("beta.kubernetes.io/arch=amd63")
            .execute();

        for (var node : nodes.getItems()) {
            System.out.println(node.getMetadata().getName());
        }
    }
}
