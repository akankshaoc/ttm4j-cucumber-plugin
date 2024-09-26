package dev.akanksha.ttm4j_cucumber_plugin.api_controller;

import dev.akanksha.ttm4j_cucumber_plugin.model.Test;
import dev.akanksha.ttm4j_cucumber_plugin.model.TestSchema;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;

import static dev.akanksha.ttm4j_cucumber_plugin.api_controller.ProjectSettings.apiKey;
import static dev.akanksha.ttm4j_cucumber_plugin.api_controller.ProjectSettings.projectKey;
import static io.restassured.RestAssured.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TestCaseController {

    static {
        baseURI = "https://api.ttm4j.tricentis.com/";
    }

    public List<Test> fetchAllTestCases() {
        return given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .basePath("/v1/projects/{project-key}/test-cases")
                .pathParam("project-key", projectKey)
                .log().all()
                .when()
                .get()
                .jsonPath()
                .get("items");
    }

    public String postTestCase(List<Test> tests) {
        TestSchema testSchema = TestSchema.builder().tests(tests).build();
        return given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .basePath("/v1/projects/{project-key}/test-cases")
                .pathParam("project-key", projectKey)
                .body(testSchema)
                .when()
                .post()
                .jsonPath()
                .get("jobId");
    }

    public Optional<String> fetchTestCaseKeysWithSummary(String summary) {
        //todo: change to jql for absolute matching
        String jql = "project = "
                + projectKey
                + " AND summary ~ '"
                + summary
                +"'";

        Map testcase = given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .basePath("/v1/test-cases/search")
                .body("{\"jql\" : \""+ jql +"\"}")
                .when()
                .post()
                .jsonPath()
                .get("items[0]");

        return Optional.ofNullable(
                testcase != null ? testcase.get("key").toString() : null
        );
    }

    public Optional<Test> fetchTestCaseWithSummary(String summary) {
        //todo: change for jql with absolute matching
        String jql = "project = "
                + projectKey
                + " AND summary ~ '"
                + summary
                +"'";

        Test testcase = given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .basePath("/v1/test-cases/search")
                .body("{\"jql\" : \""+ jql +"\"}")
                .when()
                .post()
                .jsonPath()
                .getObject("items[0]", Test.class);

        return Optional.ofNullable(testcase);
    }

    public void updateTestCase(String testKey, Test updatedTest) {
        given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .basePath("/v1/projects/{project-key}/test-cases/{key}")
                .pathParam("project-key", projectKey)
                .pathParam("key", testKey)
                .body(updatedTest)
                .when()
                .put();
    }
}
