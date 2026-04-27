package com.automation.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Smoke test runner — executes only @smoke tagged scenarios.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.automation.tests.stepdefinitions", "com.automation.tests.hooks"},
        plugin = {
                "pretty",
                "json:target/cucumber-reports/smoke-cucumber.json",
                "html:target/cucumber-reports/smoke-cucumber.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.automation.framework.reporting.FeatureCategoryPlugin"
        },
        tags = "@smoke",
        monochrome = true
)
public class SmokeTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
