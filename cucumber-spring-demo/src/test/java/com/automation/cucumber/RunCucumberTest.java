package com.automation.cucumber;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		 plugin = { "pretty", "html:target/cucumber-reports" }
		)
public class RunCucumberTest {
}
