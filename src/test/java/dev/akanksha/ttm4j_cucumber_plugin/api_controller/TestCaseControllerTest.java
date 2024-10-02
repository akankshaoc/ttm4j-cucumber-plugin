package dev.akanksha.ttm4j_cucumber_plugin.api_controller;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TestCaseControllerTest {

    TestCaseController controller = new TestCaseController();

    @Test
    void linkRequirement() {
        controller.linkRequirement("SCRUM-52", Collections.singletonList("SCRUM-12"));
    }
}