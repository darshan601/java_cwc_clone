package com.codingchallenge.ccwc.formatter;

import com.codingchallenge.ccwc.cli.CcwcOptions;

/**
 * Interface for formatting output results. This allows for different output formats to be supported
 * in the future.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public interface OutputFormatter {

    /**
     * Formats the analysis result for output.
     * 
     * @param result the raw analysis result
     * @param options the options used for analysis
     * @return formatted output string
     */
    String format(String result, CcwcOptions options);
}
