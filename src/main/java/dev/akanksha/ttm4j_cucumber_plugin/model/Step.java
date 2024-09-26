package dev.akanksha.ttm4j_cucumber_plugin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Step {
    private String expectedResult;
    private String data;
    private String description;
    private String testCaseKey;
    private String status;
    private String actualResult;
    private String comment;
}

