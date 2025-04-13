package com.fashiontrunk.fashiontrunkapi.Services;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public abstract class ContainerServiceBase {

    protected final RestTemplate restTemplate = new RestTemplate();

    protected abstract String getContainerName();
    protected abstract String getImageName();
    protected abstract int getPort();
    protected abstract String getHealthCheckUrl();

    protected void ensureContainerRunning() throws IOException, InterruptedException {
        Process check = new ProcessBuilder("docker", "ps", "-q", "-f", "name=" + getContainerName()).start();
        String output = new String(check.getInputStream().readAllBytes());

        if (output.trim().isEmpty()) {
            new ProcessBuilder(
                    "docker", "run", "--rm", "-d",
                    "--name", getContainerName(),
                    "-p", getPort() + ":" + getPort(),
                    getImageName()
            ).start();

            waitForServiceReady(getHealthCheckUrl());
        }
    }

    protected void waitForServiceReady(String url) throws InterruptedException {
        int attempts = 0;
        while (attempts++ < 10) {
            try {
                restTemplate.getForEntity(url, String.class);
                return;
            } catch (Exception e) {
                Thread.sleep(2000);
            }
        }
        throw new RuntimeException("Service failed to start after timeout.");
    }
}
