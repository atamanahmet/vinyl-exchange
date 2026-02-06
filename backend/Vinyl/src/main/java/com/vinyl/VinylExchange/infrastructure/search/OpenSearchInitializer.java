package com.vinyl.VinylExchange.infrastructure.search;

import java.net.HttpURLConnection;
import java.net.URL;

import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

// TODO: remove, devonly
@Component
@RequiredArgsConstructor
public class OpenSearchInitializer {

    private final Logger log = LoggerFactory.getLogger(OpenSearchInitializer.class);

    private final RestHighLevelClient openSearchClient;

    // check open search client is running, if not, start docker container;
    @PostConstruct
    public void init() throws InterruptedException {

        if (!isOpenSearchRunning()) {

            startDockerCompose();
            waitForOpenSearch();
        }

        log.info("OpenSearch client container is running");

    }

    private void startDockerCompose() {

        String projectRoot = System.getProperty("user.dir");

        try {

            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "compose",
                    "-f",
                    projectRoot + "/infrastructure/search/docker/docker-compose.opensearch.yml",
                    "up", "-d");

            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to start OpenSearch via Docker", e);
        }
    }

    private void waitForOpenSearch() {
        int retries = 20;

        while (retries-- > 0) {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL("https://localhost:9200").openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(1000);
                con.setReadTimeout(1000);
                con.connect();

                if (con.getResponseCode() == 200) {
                    return;
                }
            } catch (Exception e) {
            }

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
        }

        throw new IllegalStateException("OpenSearch is not ready, adjust retries");
    }

    private boolean isOpenSearchRunning() {
        try {
            openSearchClient.ping(RequestOptions.DEFAULT);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
