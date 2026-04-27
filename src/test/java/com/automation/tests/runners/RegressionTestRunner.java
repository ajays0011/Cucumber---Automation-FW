package com.automation.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Regression test runner — executes only @regression tagged scenarios.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.automation.tests.stepdefinitions", "com.automation.tests.hooks"},
        plugin = {
                "pretty",
                "json:target/cucumber-reports/regression-cucumber.json",
                "html:target/cucumber-reports/regression-cucumber.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.automation.framework.reporting.FeatureCategoryPlugin"
        },
        tags = "@regression",
        monochrome = true
)
public class RegressionTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
