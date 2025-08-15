package com.codingchallenge.ccwc;

import com.codingchallenge.ccwc.cli.CommandLineParser;
import com.codingchallenge.ccwc.cli.CcwcOptions;
import com.codingchallenge.ccwc.service.FileAnalysisService;
import com.codingchallenge.ccwc.service.impl.FileAnalysisServiceImpl;
import com.codingchallenge.ccwc.exception.CcwcException;
import com.codingchallenge.ccwc.formatter.OutputFormatter;
import com.codingchallenge.ccwc.formatter.impl.DefaultOutputFormatter;

/**
 * Main application class for the ccwc (Coding Challenge Word Count) utility. This is a Java
 * implementation of the Unix wc command line tool.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class CcwcApplication {

    private final CommandLineParser commandLineParser;
    private final FileAnalysisService fileAnalysisService;
    private final OutputFormatter outputFormatter;

    /**
     * Default constructor that initializes the application with default implementations.
     */
    public CcwcApplication() {
        this.commandLineParser = new CommandLineParser();
        this.fileAnalysisService = new FileAnalysisServiceImpl();
        this.outputFormatter = new DefaultOutputFormatter();
    }

    /**
     * Constructor for dependency injection (useful for testing and extensibility).
     * 
     * @param commandLineParser the command line parser
     * @param fileAnalysisService the file analysis service
     * @param outputFormatter the output formatter
     */
    public CcwcApplication(CommandLineParser commandLineParser,
            FileAnalysisService fileAnalysisService, OutputFormatter outputFormatter) {
        this.commandLineParser = commandLineParser;
        this.fileAnalysisService = fileAnalysisService;
        this.outputFormatter = outputFormatter;
    }

    /**
     * Main entry point of the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        CcwcApplication application = new CcwcApplication();
        int exitCode = application.run(args);
        System.exit(exitCode);
    }

    /**
     * Runs the application with the provided command line arguments.
     * 
     * @param args command line arguments
     * @return exit code (0 for success, 1 for error)
     */
    public int run(String[] args) {
        try {
            CcwcOptions options = commandLineParser.parse(args);
            String result = fileAnalysisService.analyze(options);
            String formattedOutput = outputFormatter.format(result, options);
            System.out.println(formattedOutput);
            return 0;
        } catch (CcwcException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return 1;
        }
    }
}
