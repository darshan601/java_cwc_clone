package com.codingchallenge.ccwc.formatter.impl;

import com.codingchallenge.ccwc.cli.CcwcOptions;
import com.codingchallenge.ccwc.formatter.OutputFormatter;

/**
 * Default implementation of OutputFormatter. Provides standard Unix wc-like output formatting.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class DefaultOutputFormatter implements OutputFormatter {

    @Override
    public String format(String result, CcwcOptions options) {
        // For now, just return the result as-is since the formatting
        // is already done in the service layer
        // This separation allows for future enhancements like JSON output, CSV output, etc.
        return result;
    }
}
