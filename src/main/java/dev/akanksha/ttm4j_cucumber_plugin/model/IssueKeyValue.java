package dev.akanksha.ttm4j_cucumber_plugin.model;

import lombok.Data;

@Data
public class IssueKeyValue {
    private String issueKey;
    public IssueKeyValue(String s) {
        this.issueKey = s;
    }
}
