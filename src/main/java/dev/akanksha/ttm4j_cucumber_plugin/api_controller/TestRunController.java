package dev.akanksha.ttm4j_cucumber_plugin.api_controller;

import dev.akanksha.ttm4j_cucumber_plugin.model.TestRun;
import dev.akanksha.ttm4j_cucumber_plugin.model.TestRunSchema;
import io.restassured.http.ContentType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dev.akanksha.ttm4j_cucumber_plugin.api_controller.ProjectSettings.apiKey;
import static dev.akanksha.ttm4j_cucumber_plugin.api_controller.ProjectSettings.projectKey;
import static io.restassured.RestAssured.*;

public class TestRunController {
    static {
        baseURI = "https://api.ttm4j.tricentis.com/";
    }

    public void postTestRun(TestRun testRun) {
        given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .basePath("/v1/projects/{project-key}/test-runs")
                .pathParam("project-key", projectKey)
                .body(TestRunSchema.builder()
                        .testRuns(Collections.singletonList(testRun))
                        .cycleName("automated-test-cycle" + LocalDate.now())
                        .build())
                .when()
                .post()
                .jsonPath()
                .get("jobId");
    }
}
