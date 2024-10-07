# TTM4J Cucumber Plugin

## Table of Contents
- Introduction
- Features
- Installation
- Usage
- Example

## Introduction
Cucumber Java Test Utility is a robust and easy-to-use plugin for writing and running automated tests using Cucumber and tracking the same in Jira Projects. Utilizing the Tricentis Test Management Tool, it simplifies and automates the process reporting in behavior-driven development (BDD) driven tests.

## Features
- **Easy Integration**: Seamlessly integrates with existing cucumber projects in java
- **Customizable**: runs independent of the underlying testing engine
- **Design and Run**: Publishes data on Test Design as well as Test Executions

## Installation
To get started, download the latest jar and add it to you classpath

## Usage

1. Configuring Project Settings
    - get tricentis test management [project keys](https://documentation.tricentis.com/tricentis_test_management_for_jira/content/admins/settings.htm#API_Keys)
    - in `test/resources/cucumber.properties` file, add the following properties
        ```properties
        ttm4j.projectKey=<project-key>
        ttm4j.apiKey=<ttmj-key>
        ```
2. Plugin Listener
    - plugin the reporter listener `dev.akanksha.ttm4j_cucumber_plugin.ReporterListeners` from the utility to your test runner
    - If you are using JUnit 5, this can be done in the `cucumber.properties` file as well
        ```properties
        cucumber.plugin=dev.akanksha.ttm4j_cucumber_plugin.ReporterListeners
        ```
3. Writing Feature Files
    utilise tags for enabling tracking on TTMJ
    - @TTMJTest : tracks scenario(s) as test under test design
    - @TTMJTestCycle(name) : tracks all associated runs in cycle with name 
    - @TTMJRequirement(key) : links all tests with requirement with the given key
    - @TTMJFolder(path) : places tests under the specified folder


## Example

```gherkin
@TTMJTestCycle(cycle-name) # all tests ran will be updated with cycle name
Feature: An example

Background:
* an example scenario
* all step definitions are implemented

@TTMJTest # tag any scenario you wish to track as test here
Scenario: The example    
Given an example scenario
When all step definitions are implemented
Then the scenario passes

@TTMJTest
@TTMJFolder(a/b) # test case placed under teh specified folder path in test design 
Scenario: eating apples
# Step 1
Given I had "7" apples
When I eat "2"
Then I should be left with "5" apples
# Step 2
Given I had appetite for "2" apples
Then I must not be hungry

@TTMJTest
@TTMJRequirement(key) # this test is tracked with requirement with given key
Scenario: eating apples intentional fail
Given I had "7" apples
When I eat "2"
Then I should be left with "7" apples
Given I had appetite for "2" apples
Then I must not be hungry

@TTMJTest # each example is reported as a different run
Scenario Outline: example outline
Given I had "<initial>" apples
When I eat "<depleted>"
Then I should be left with "<remaining>" apples
Examples:
| initial | depleted | remaining |
|10       |8         |2          |
|40       |10        |30         |
|100      |1         |98         |
```

