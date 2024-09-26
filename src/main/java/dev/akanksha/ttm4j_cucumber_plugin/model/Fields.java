package dev.akanksha.ttm4j_cucumber_plugin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

@NoArgsConstructor
public class Fields {
    private List<String> components;
    private String summary;
    private String description;
    private String status;
    private String priority;
    private List<String> labels;
}

