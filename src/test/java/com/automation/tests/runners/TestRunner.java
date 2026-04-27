package com.automation.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Primary test runner — executes all scenarios with parallel support.
 * Generates Cucumber JSON + Allure + Extent reports.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.automation.tests.stepdefinitions", "com.automation.tests.hooks"},
        plugin = {
                "pretty",
                "json:target/cucumber-reports/cucumber.json",
                "html:target/cucumber-reports/cucumber.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.automation.framework.reporting.FeatureCategoryPlugin"
        },
        tags = "@smoke or @regression",
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
