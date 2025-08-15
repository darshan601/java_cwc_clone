package com.codingchallenge.ccwc.cli;

import com.codingchallenge.ccwc.exception.InvalidArgumentException;

/**
 * Command line parser for ccwc utility. Parses command line arguments and creates CcwcOptions
 * object.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class CommandLineParser {

    private static final String OPTION_BYTES = "-c";
    private static final String OPTION_LINES = "-l";
    private static final String OPTION_WORDS = "-w";
    private static final String OPTION_CHARS = "-m";

    /**
     * Parses command line arguments and returns CcwcOptions.
     * 
     * @param args command line arguments
     * @return parsed options
     * @throws InvalidArgumentException if arguments are invalid
     */
    public CcwcOptions parse(String[] args) throws InvalidArgumentException {
        CcwcOptions.Builder builder = CcwcOptions.builder();
        String filename = null;

        // If no arguments, read from stdin with default behavior
        if (args.length == 0) {
            return builder.filename(null).build();
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case OPTION_BYTES:
                    builder.countBytes(true);
                    break;
                case OPTION_LINES:
                    builder.countLines(true);
                    break;
                case OPTION_WORDS:
                    builder.countWords(true);
                    break;
                case OPTION_CHARS:
                    builder.countCharacters(true);
                    break;
                default:
                    if (arg.startsWith("-")) {
                        throw new InvalidArgumentException("Unknown option: " + arg);
                    } else {
                        if (filename != null) {
                            throw new InvalidArgumentException(
                                    "Multiple filenames not supported: " + filename + ", " + arg);
                        }
                        filename = arg;
                    }
            }
        }

        // If no filename provided, use stdin (represented as null)
        return builder.filename(filename).build();
    }
}
