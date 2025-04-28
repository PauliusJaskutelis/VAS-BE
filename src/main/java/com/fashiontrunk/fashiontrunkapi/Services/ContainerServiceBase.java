package com.fashiontrunk.fashiontrunkapi.Services;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;

public abstract class ContainerServiceBase {

    protected final RestTemplate restTemplate = new RestTemplate();

    protected abstract String getContainerName();
    protected abstract String getImageName();
    protected abstract int getPort();
    protected abstract String getHealthCheckUrl();

    protected void startContainer() throws IOException, InterruptedException {
        if (!isContainerRunning()) {
            new ProcessBuilder(
                    "docker", "run", "--rm", "-d",
                    "--name", getContainerName(),
                    "-p", getPort() + ":" + getPort(),
                    getImageName()
            ).start();

            waitForServiceReady(getHealthCheckUrl());
        }
    }

    protected boolean isContainerRunning() throws IOException {
        Process check = new ProcessBuilder("docker", "ps", "-q", "-f", "name=" + getContainerName()).start();
        return !new String(check.getInputStream().readAllBytes()).trim().isEmpty();
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

    protected void stopContainer() throws IOException, InterruptedException {
        Process stopProc = new ProcessBuilder("docker", "stop", this.getContainerName()).start();
        int exitCode = stopProc.waitFor();
        if(exitCode != 0) {
            System.out.println(LocalDateTime.now() + "Failed to stop the process " + this.getContainerName() + ". killing the container.");
            Process killProc = new ProcessBuilder("docker", "rm", "-f", this.getContainerName()).start();
            exitCode = killProc.waitFor();
            if(exitCode != 0) {
                throw new IOException(LocalDateTime.now() + "Failed to kill the process: " + this.getContainerName());
            }
        }
    }

    protected boolean hasModel(String url) {
        return restTemplate.getForEntity(url, String.class).getStatusCode() == HttpStatus.OK;
    }

}
