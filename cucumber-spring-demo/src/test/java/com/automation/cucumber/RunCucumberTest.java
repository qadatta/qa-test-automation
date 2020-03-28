package com.automation.cucumber;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "./src/test/resources/com/automation/cucumber/features/api_test_rest_assured.feature","./src/test/resources/com/automation/cucumber/features/api_test.feature"},
		 plugin = { "pretty", "html:target/cucumber-reports" }
		)
public class RunCucumberTest {
}
