package com.kofta.app;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import java.io.IOException;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws IOException {
        ApiClient apiClient = Config.defaultClient();

        System.out.println(apiClient.getBasePath());
        System.out.println("Hello World!");
    }
}
