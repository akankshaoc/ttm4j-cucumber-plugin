package dev.akanksha.ttm4j_cucumber_plugin;

import dev.akanksha.ttm4j_cucumber_plugin.api_controller.TestCaseController;
import dev.akanksha.ttm4j_cucumber_plugin.api_controller.TestCaseCreationTracker;
import dev.akanksha.ttm4j_cucumber_plugin.api_controller.TestRunController;
import dev.akanksha.ttm4j_cucumber_plugin.mapper.GherkinModelMapper;
import dev.akanksha.ttm4j_cucumber_plugin.mapper.TestCaseMapper;
import dev.akanksha.ttm4j_cucumber_plugin.model.Test;
import dev.akanksha.ttm4j_cucumber_plugin.model.TestRun;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.TestCase;

import java.io.IOException;
import java.util.*;

public class Utilities {

    static List<TestCaseCreationTracker> testCaseCreationTrackers = new ArrayList<>();
    static TestCaseController testCaseController = new TestCaseController();
    static TestRunController testRunController = new TestRunController();
    static List<Scenario> scenariosRan = new ArrayList<>();
    static List<TestCase> testCasesRan = new ArrayList<>();

    /**
     * creates test cases on you jira project.
     * @param featureFileDirectory : path to the directory where you store all you jira files
     * @throws IOException : when directory cannot be used
     */
    public static void postTestData(String featureFileDirectory) throws IOException {
        //1. get all test case to be created
        List<Test> tests = GherkinModelMapper
                .forFeatureFilesInDirectory(featureFileDirectory)
                .getAllTestCases();

        //2. find all test cases that already exists and update them
        List<Test> toCreate = new ArrayList<>();

        for(Test test : tests) {
            Optional<String> key= testCaseController.fetchTestCaseKeysWithSummary(test.getFields().getSummary());
            if(key.isPresent()) {
                /*
                //todo: find some solution here
                 */
            }
            else toCreate.add(test);
        }

        if(toCreate.isEmpty()) return;
        //4. create the new test cases
        String jobId = testCaseController.postTestCase(toCreate);
        TestCaseCreationTracker testCaseCreationTracker = new TestCaseCreationTracker(jobId, 3000);
        testCaseCreationTrackers.add(testCaseCreationTracker);
        testCaseCreationTracker.start();
    }

    public static void postTestData(TestCase testCase) {
        Test test = TestCaseMapper.getTestFrom(testCase);

        Optional<String> key= testCaseController.fetchTestCaseKeysWithSummary(test.getFields().getSummary());
        if(key.isPresent()) {
            /*
            //todo: find some solution here to update
             */
            return;
        }

        String jobId = testCaseController.postTestCase(Collections.singletonList(test));
        TestCaseCreationTracker testCaseCreationTracker = new TestCaseCreationTracker(jobId, 3000);
        testCaseCreationTrackers.add(testCaseCreationTracker);
        testCaseCreationTracker.start();
    }

    public static void captureTestRuns(Scenario scenario) {
        scenariosRan.add(scenario);
    }

    public static void captureTestRuns(TestCase testCase) {
        testCasesRan.add(testCase);
    }

    public static void postTestRunData() throws InterruptedException {
        //1. join thread listening for test case creation
        for(TestCaseCreationTracker testCaseCreationTracker : testCaseCreationTrackers) testCaseCreationTracker.join();

        //2. for each scenario ran,
        //2.1. find related test-key
        //2.2. post tes run with test key
        for(Scenario scenario : scenariosRan) {
            Optional<Test> test = testCaseController.fetchTestCaseWithSummary(scenario.getName());
            if(!test.isPresent()) continue;

            TestRun testRun = TestRun.builder()
                    .testCaseKey(test.get().getKey())
                    .status(scenario.getStatus().name())
                    .testType(test.get().getTestType())
                    .description(test.get().getFields().getDescription())
                    .priority(test.get().getFields().getPriority())
            .build();

            testRunController.postTestRun(testRun);
        }

        //3. for each test case ran,
        //3.1. find related test key
        //3.2. post test run with test key
        for(Scenario scenario : scenariosRan) {
            Optional<Test> test = testCaseController.fetchTestCaseWithSummary(scenario.getName());
            if(!test.isPresent()) continue;

            TestRun testRun = TestRun.builder()
                    .testCaseKey(test.get().getKey())
                    .status(scenario.getStatus().name())
                    .testType(test.get().getTestType())
                    .description(test.get().getFields().getDescription())
                    .priority(test.get().getFields().getPriority())
                    .build();

            testRunController.postTestRun(testRun);
        }


    }
}
