package com.automation.framework.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TestNG Retry Analyzer for flaky test handling.
 * Automatically retries failed tests up to a configurable max count.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int MAX_RETRY = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            LOG.warn("⟳ Retrying test '{}' — attempt {}/{}", 
                    result.getName(), retryCount, MAX_RETRY);
            return true;
        }
        return false;
    }
}
