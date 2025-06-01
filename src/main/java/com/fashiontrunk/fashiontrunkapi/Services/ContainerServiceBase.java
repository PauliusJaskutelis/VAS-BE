package com.fashiontrunk.fashiontrunkapi.Services;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;

public abstract class ContainerServiceBase {

    protected final RestTemplate restTemplate = new RestTemplate();

    // --- Abstract methods for static fallback use-case ---
    protected abstract String getContainerName();
    protected abstract String getImageName();
    protected abstract int getPort();
    protected abstract String getHealthCheckUrl();

    // --- Legacy default implementation ---
    protected void startContainer() throws IOException, InterruptedException {
        startContainer(getContainerName(), getPort(), getImageName(), getHealthCheckUrl());
    }

    protected boolean isContainerRunning() throws IOException {
        return isContainerRunning(getContainerName());
    }

    protected void stopContainer() throws IOException, InterruptedException {
        stopContainer(getContainerName());
    }

    // --- New dynamic methods ---

    protected void startContainer(String containerName, int port, String imageName, String healthCheckUrl)
            throws IOException, InterruptedException {

        if (!isContainerRunning(containerName)) {
            new ProcessBuilder(
                    "docker", "run", "--rm", "-d",
                    "--name", containerName,
                    "-p", port + ":5000", // container always uses 5000 inside
                    imageName
            ).inheritIO().start();

            waitForServiceReady(healthCheckUrl);
        }
    }

    protected boolean isContainerRunning(String containerName) throws IOException {
        Process check = new ProcessBuilder("docker", "ps", "-q", "-f", "name=" + containerName).start();
        return !new String(check.getInputStream().readAllBytes()).trim().isEmpty();
    }

    protected void stopContainer(String containerName) throws IOException, InterruptedException {
        Process stopProc = new ProcessBuilder("docker", "stop", containerName).start();
        int exitCode = stopProc.waitFor();
        if (exitCode != 0) {
            System.out.println(LocalDateTime.now() + " Failed to stop: " + containerName + ". Trying to kill.");
            Process killProc = new ProcessBuilder("docker", "rm", "-f", containerName).start();
            exitCode = killProc.waitFor();
            if (exitCode != 0) {
                throw new IOException(LocalDateTime.now() + " Failed to kill: " + containerName);
            }
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

    protected boolean hasModel(String url) {
        return restTemplate.getForEntity(url, String.class).getStatusCode() == HttpStatus.OK;
    }
}
