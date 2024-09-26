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
public class TestRun {
    private String testCaseKey;
    private String precondition;
    private String status;
    private String name;
    private String description;
    private String priority;
    private String testCategory;
    private TestType testType;
    private String assignee;
    private String executedBy;
    private String plannedStartDate;
    private String plannedEndDate;
    private Automation automation;
    private List<String> components;
    private List<String> labels;
    private String cycleName;
    private String folderPath;
    private List<Step> steps;
}

