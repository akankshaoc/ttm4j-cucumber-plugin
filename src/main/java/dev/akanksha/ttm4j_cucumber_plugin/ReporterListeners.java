package dev.akanksha.ttm4j_cucumber_plugin;

import dev.akanksha.ttm4j_cucumber_plugin.api_controller.TestCaseController;
import dev.akanksha.ttm4j_cucumber_plugin.api_controller.TestCaseCreationTracker;
import dev.akanksha.ttm4j_cucumber_plugin.api_controller.TestRunController;
import dev.akanksha.ttm4j_cucumber_plugin.mapper.TestCaseMapper;
import dev.akanksha.ttm4j_cucumber_plugin.model.Step;
import dev.akanksha.ttm4j_cucumber_plugin.model.Test;
import dev.akanksha.ttm4j_cucumber_plugin.model.TestRun;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;

import java.util.*;

public class ReporterListeners implements EventListener {
    static TestCaseController testCaseController = new TestCaseController();
    static TestRunController testRunController = new TestRunController();
    static Map<TestCase, TestRun> testRuns = new HashMap<>();
    static Map<TestCase, Step> testRunRunningStep = new HashMap<>();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::testCaseStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::testStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::testCaseFinished);
    }

    public void testCaseStarted(TestCaseStarted testCaseStarted) {
        //1. gather data to publish TTM4J Test Case
        TestCase runningTest = testCaseStarted.getTestCase();
        if(runningTest.getTags().stream().noneMatch(t -> t.trim().equals("@TTMJTest"))) return;
        Test testToPublish = TestCaseMapper.getTestFrom(runningTest);

        //2. see if test case with data [summary = scenario name] already exist
        Optional<String> key= testCaseController.fetchTestCaseKeysWithSummary(runningTest.getName());
        if(key.isPresent()) {
            /*
            //2.1. todo: find some solution here to update the steps (in case feature file has changed)
             */
        } else {
            //3. wait for test case creation
            try {
                String jobId = testCaseController.postTestCase(Collections.singletonList(testToPublish));
                TestCaseCreationTracker testCaseCreationTracker = new TestCaseCreationTracker(jobId, 1000);
                testCaseCreationTracker.start();
                testCaseCreationTracker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //4. record test run
        key = testCaseController.fetchTestCaseKeysWithSummary(runningTest.getName());
        key.ifPresent(s -> testRuns.put(runningTest, TestRun.builder().testCaseKey(s).build()));
    }

    public void testStepFinished(TestStepFinished testStepFinished) {
        TestCase testCase = testStepFinished.getTestCase();
        TestStep testStep = testStepFinished.getTestStep();
        if(!testRuns.containsKey(testCase)) return;
        TestRun ttm4jTest = testRuns.get(testCase);

        if(!(testStep instanceof PickleStepTestStep)) return;
        PickleStepTestStep pickleStepTestStep = (PickleStepTestStep) testStep;
        if(pickleStepTestStep.getStep().getKeyword().trim().equals("Then")) {
            Step step = testRunRunningStep.get(testCase);
            step.setExpectedResult(pickleStepTestStep.getStep().getKeyword() + pickleStepTestStep.getStep().getText());
            if(testStepFinished.getResult().getStatus().isOk()) {
                step.setActualResult(pickleStepTestStep.getStep().getKeyword() + pickleStepTestStep.getStep().getText());
                step.setStatus("PASSED");
            } else{
                step.setActualResult(testStepFinished.getResult().getError().getMessage());
                step.setStatus("FAILED");
            }
            testRunRunningStep.put(testCase, null);
            List<Step> steps = ttm4jTest.getSteps();
            if(steps == null) steps = new ArrayList<>();
            steps.add(step);
            ttm4jTest.setSteps(steps);
            testRuns.put(testCase, ttm4jTest);

        } else {
            Step step = testRunRunningStep.get(testCase);
            if(step == null) {
                step = Step.builder().status("UNEXECUTED").build();
            }

            String description = step.getDescription();
            if(description == null) description = "";
            description += pickleStepTestStep.getStep().getKeyword() + pickleStepTestStep.getStep().getText();
            step.setDescription(description);
            testRunRunningStep.put(testCase, step);
        }
    }

    public void testCaseFinished(TestCaseFinished testCaseFinished) {
        if(!testRuns.containsKey(testCaseFinished.getTestCase())) return;
        TestRun testRun = testRuns.get(testCaseFinished.getTestCase());
        testRun.setStatus(testCaseFinished.getResult().getStatus().toString());
        Optional<String> testCycleName = testCaseFinished
                .getTestCase()
                .getTags()
                .stream()
                .filter(t -> t.startsWith("@TTMJTestCycle(") && t.endsWith(")"))
                .findFirst()
                .map(s -> s.substring(15, s.lastIndexOf(')')));
        if(testCycleName.isPresent()) {
            testRunController.postTestRun(testRun, testCycleName.get());
        } else {
            testRunController.postTestRun(testRun);
        }
    }
}
