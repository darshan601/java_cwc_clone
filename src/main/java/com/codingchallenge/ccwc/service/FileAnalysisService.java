package com.codingchallenge.ccwc.service;

import com.codingchallenge.ccwc.cli.CcwcOptions;
import com.codingchallenge.ccwc.exception.CcwcException;

/**
 * Service interface for analyzing files. This interface follows the Strategy pattern to allow for
 * different analysis implementations.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public interface FileAnalysisService {

    /**
     * Analyzes a file based on the provided options and returns the result.
     * 
     * @param options the analysis options
     * @return the analysis result
     * @throws CcwcException if analysis fails
     */
    String analyze(CcwcOptions options) throws CcwcException;
}
