package dev.akanksha.ttm4j_cucumber_plugin.mapper;

import dev.akanksha.ttm4j_cucumber_plugin.model.*;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestCaseMapper {
    public static Test getTestFrom(TestCase testCase) {
        Fields fields = Fields
                .builder()
                .summary(testCase.getName())
                .status(TestStatus.UNEXECUTED.toString())
                .build();

        List<dev.akanksha.ttm4j_cucumber_plugin.model.Step> steps = new ArrayList<>();
        dev.akanksha.ttm4j_cucumber_plugin.model.Step running = dev.akanksha.ttm4j_cucumber_plugin.model.Step.builder().build();
        for (TestStep s : testCase.getTestSteps()) {
            if(!(s instanceof PickleStepTestStep)) continue;
            io.cucumber.plugin.event.Step step = ((PickleStepTestStep) s).getStep();
            if (step.getKeyword().trim().equals("Then")) {
                running.setExpectedResult(step.getKeyword() + step.getText());
                steps.add(running);
                running = dev.akanksha.ttm4j_cucumber_plugin.model.Step.builder().build();
            } else {
                running.setDescription(
                        running.getDescription() == null ? "" : running.getDescription()
                                + step.getKeyword() + step.getText()
                );
            }
        }


        TtmFields ttmFields = TtmFields
                .builder()
                .steps(steps)
                .build();

        Test build = Test.builder()
                .fields(fields)
                .testType(TestType.Manual)
                .ttmFields(ttmFields)
                .build();

        if(getFolderNameFrom(testCase).isPresent()) {
            build.setFolder(getFolderNameFrom(testCase).get());
        }

        return build;
    }

    public static List<String> getRequirementKeysFrom(TestCase testCase) {
        return testCase
                .getTags()
                .stream()
                .filter(s -> s.startsWith("@TTMJRequirement(") && s.endsWith(")"))
                .map(s -> s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")))
                .collect(Collectors.toList());
    }

    public static Optional<String> getFolderNameFrom(TestCase testCase) {
        return testCase
                .getTags()
                .stream()
                .filter(s -> s.startsWith("@TTMJFolder(") && s.endsWith(")"))
                .findFirst()
                .map(s -> s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")));
    }
}
