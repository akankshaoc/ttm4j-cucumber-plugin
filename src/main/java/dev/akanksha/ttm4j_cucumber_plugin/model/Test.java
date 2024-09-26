package dev.akanksha.ttm4j_cucumber_plugin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Test {
    private String id;
    private String key;
    private String project;
    private String testCategory;
    private TestType testType;
    private Fields fields;
    private TtmFields ttmFields;
    private Automation automation;
    private List<String> requirements;
    private String folder;
    private UUID folderId;
    private Object customFields;
}
