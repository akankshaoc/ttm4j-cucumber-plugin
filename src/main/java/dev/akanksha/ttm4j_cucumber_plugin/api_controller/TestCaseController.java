package dev.akanksha.ttm4j_cucumber_plugin.api_controller;

import dev.akanksha.ttm4j_cucumber_plugin.model.IssueKeyValue;
import dev.akanksha.ttm4j_cucumber_plugin.model.Test;
import dev.akanksha.ttm4j_cucumber_plugin.model.TestSchema;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static dev.akanksha.ttm4j_cucumber_plugin.api_controller.ProjectSettings.apiKey;
import static dev.akanksha.ttm4j_cucumber_plugin.api_controller.ProjectSettings.projectKey;
import static io.restassured.RestAssured.*;

import java.util.*;
import java.util.stream.Collectors;

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
        return fetchTestCaseWithSummary(summary).map(Test::getKey);
    }

    public Optional<Test> fetchTestCaseWithSummary(String summary) {
        String jql = "project = "
                + projectKey
                + " AND summary ~ '"
                + summary
                +"'";

        Test[] testcases = given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .basePath("/v1/test-cases/search")
                .body("{\"jql\" : \""+ jql +"\"}")
                .when()
                .post()
                .jsonPath()
                .getObject("items", Test[].class);

        for(Test testcase : testcases) {
            if(testcase.getFields().getSummary().equals(summary)) return Optional.of(testcase);
        }

        return Optional.empty();
    }

    public void updateTestCase(String testKey, Test updatedTest) {
        //todo: this isn't working
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

    public void linkRequirement(String testKey, List<String> requirementKeys) {
        given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + apiKey)
                .basePath("/v1/projects/{project-key}/test-cases/{test-key}/requirement/link")
                .pathParam("project-key", projectKey)
                .pathParam("test-key", testKey)
                .body(requirementKeys.stream().map(IssueKeyValue::new).collect(Collectors.toList()))
                .post();
    }
}
