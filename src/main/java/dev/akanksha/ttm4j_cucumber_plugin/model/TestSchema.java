package dev.akanksha.ttm4j_cucumber_plugin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestSchema {
    private String folder;
    private UUID folderId;
    private String callbackUrl;
    private List<Test> tests;
}

