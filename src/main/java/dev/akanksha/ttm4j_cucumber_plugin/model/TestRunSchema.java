package dev.akanksha.ttm4j_cucumber_plugin.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestRunSchema {
    private String version;
    private String cycleName;
    private String folderPath;
    private Boolean includeAttachments;
    private List<TestRun> testRuns;
}

