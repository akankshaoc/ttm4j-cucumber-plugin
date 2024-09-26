package dev.akanksha.ttm4j_cucumber_plugin.api_controller;

import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;

import static dev.akanksha.ttm4j_cucumber_plugin.api_controller.ProjectSettings.apiKey;
import static dev.akanksha.ttm4j_cucumber_plugin.api_controller.ProjectSettings.projectKey;
import static io.restassured.RestAssured.*;

import java.util.List;

@RequiredArgsConstructor
public class TestCaseCreationTracker extends Thread {

    private final String jobId;
    private final long sleep;

    @Override
    public void run() {
        try {
            checkStatus();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkStatus() throws InterruptedException {
        while (true) {
            String status = given()
                    .basePath("/v1/projects/{projectKey}/jobs/{jobId}")
                    .pathParam("projectKey", projectKey)
                    .pathParam("jobId", jobId)
                    .contentType(ContentType.JSON)
                    .headers("Authorization", "Bearer " + apiKey)
                    .when().get()
                    .body().jsonPath().get("status");
            if(status.equals("Completed") || status.equals("Failed")) return;
            Thread.sleep(sleep);
        }
    }

    public List<String> getIssueKeys() {
        return given()
                .basePath("/v1/projects/{projectKey}/jobs/{jobId}")
                .pathParam("projectKey", projectKey)
                .pathParam("jobId", jobId)
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .when().get()
                .body().jsonPath().get("issues*.key");
    }
}
