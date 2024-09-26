package dev.akanksha.ttm4j_cucumber_plugin.mapper;

import dev.akanksha.ttm4j_cucumber_plugin.model.*;
import dev.akanksha.ttm4j_cucumber_plugin.model.Step;
import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GherkinModelMapper {
    private static final GherkinModelMapper instance = new GherkinModelMapper();
    private List<GherkinDocument> documents;

    /**
     * finds all nested feature files in a given directory and takes
     * all of them under consideration
     * @param directoryPath to you feature files
     * @return singleton instance of type GherkinModelMapper
     * @throws IOException when directory not found
     */
    public static GherkinModelMapper forFeatureFilesInDirectory(String directoryPath) throws IOException {
        List<GherkinDocument> res = new ArrayList<>();
        for(Path path : getFeatureFilePaths(Paths.get(directoryPath))) {
            res.add(getGherkinDocument(path));
        }
        instance.documents = res;
        return instance;
    }

    /**
     * takes feature file under consideration
     * @param filePath to your feature file
     * @return singleton instance of type GherkinModelMapper
     * @throws IOException when feature file not found
     */
    public static GherkinModelMapper forFeatureFile(String filePath) throws IOException {
        List<GherkinDocument> res = new ArrayList<>();
        res.add(getGherkinDocument(Paths.get(filePath)));
        instance.documents = res;
        return instance;
    }

    /**
     * chain after setting feature file path or feature file directory path
     * @return list of all test cases in feature files under consideration
     */
    public List<Test> getAllTestCases() {
        List<Test> res = new ArrayList<>();
        for(GherkinDocument document : instance.documents) {
            res.addAll(parseTests(document));
        }
        return res;
    }

    /**
     * todo: return keys of all requirements tagged with any scenario
     * @return return keys of all requirements tagged with any scenario
     */
//    public static List<String> getAllRequirementKeys() {
//        return null;
//    }


    // HELPER METHODS
    private static List<Path> getFeatureFilePaths(Path directory) throws IOException {
        List<Path> res = new ArrayList<>();

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".feature")) {
                    res.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

        });

        return res;
    }
    private static GherkinDocument getGherkinDocument(Path path) throws IOException {
        GherkinParser parser = GherkinParser.builder().build();
        return parser
                .parse(path)
                .map(Envelope::getGherkinDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
                .get(0);
    }
    private static List<Test> parseTests(GherkinDocument document) {
        List<Test> res = new ArrayList<>();
        if(!document.getFeature().isPresent()) return res;

        String precondition = null;

        for(FeatureChild featureChild : document.getFeature().get().getChildren()) {
            if(featureChild.getBackground().isPresent()) {
                precondition = featureChild
                        .getBackground()
                        .get()
                        .getSteps()
                        .stream()
                        .map(step -> step.getKeyword() + step.getText())
                        .collect(Collectors.joining("\n"));
            } else if(featureChild.getScenario().isPresent()
                    && featureChild
                    .getScenario()
                    .get().getTags()
                    .stream()
                    .anyMatch(t -> t.getName().trim().equals("@TTM4JTest"))) {
                Test test = Test.builder()
                        .testType(TestType.Manual)
                        .fields(
                                Fields.builder()
                                        .summary(featureChild
                                                .getScenario()
                                                .get()
                                                .getName()
                                        )
                                        .description(featureChild
                                                .getScenario()
                                                .get()
                                                .getDescription()
                                        )
                                        .build()
                        )
                        .ttmFields(
                                TtmFields.builder()
                                        .precondition(precondition)
                                        .steps(getTestCaseSteps(featureChild
                                                .getScenario()
                                                .get()))
                                        .build()
                        )
                        .build();
                res.add(test);
            }
        }

        return res;
    }
    private static List<Step> getTestCaseSteps(Scenario scenario) {
        List<Step> res = new ArrayList<>();

        Step step = Step.builder().build();
        for(io.cucumber.messages.types.Step gherkinStep : scenario.getSteps()) {
            if(gherkinStep.getKeyword().trim().equals("Given") || gherkinStep.getKeyword().trim().equals("When")) {
                step.setDescription(
                        (step.getDescription() == null ? "" : step.getDescription() + " ")
                                + gherkinStep.getKeyword() + gherkinStep.getText()
                );
            } else if(gherkinStep.getKeyword().trim().equals("Then")) {
                step.setExpectedResult(gherkinStep.getKeyword() + gherkinStep.getText());
                res.add(step);
                step = Step.builder().build();
            }
        }
        return res;
    }
}
