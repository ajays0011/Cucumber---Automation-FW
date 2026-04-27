package com.automation.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * API-only test runner — executes only @api tagged scenarios.
 * No WebDriver initialization for API tests.
 */
@CucumberOptions(
        features = "src/test/resources/features/api",
        glue = {"com.automation.tests.stepdefinitions", "com.automation.tests.hooks"},
        plugin = {
                "pretty",
                "json:target/cucumber-reports/api-cucumber.json",
                "html:target/cucumber-reports/api-cucumber.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.automation.framework.reporting.FeatureCategoryPlugin"
        },
        tags = "@api",
        monochrome = true
)
public class ApiTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
