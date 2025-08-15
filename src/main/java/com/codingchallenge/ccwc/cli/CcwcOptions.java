package com.codingchallenge.ccwc.cli;

/**
 * Data class representing the parsed command line options for ccwc. This class follows the builder
 * pattern for extensibility.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class CcwcOptions {

    private final boolean countBytes;
    private final boolean countLines;
    private final boolean countWords;
    private final boolean countCharacters;
    private final String filename;

    private CcwcOptions(Builder builder) {
        this.countBytes = builder.countBytes;
        this.countLines = builder.countLines;
        this.countWords = builder.countWords;
        this.countCharacters = builder.countCharacters;
        this.filename = builder.filename;
    }

    public boolean isCountBytes() {
        return countBytes;
    }

    public boolean isCountLines() {
        return countLines;
    }

    public boolean isCountWords() {
        return countWords;
    }

    public boolean isCountCharacters() {
        return countCharacters;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Returns true if reading from standard input.
     */
    public boolean isReadingFromStdin() {
        return filename == null;
    }

    /**
     * Returns true if no specific count options are specified. In this case, all counts should be
     * displayed (default behavior).
     */
    public boolean isDefaultMode() {
        return !countBytes && !countLines && !countWords && !countCharacters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean countBytes = false;
        private boolean countLines = false;
        private boolean countWords = false;
        private boolean countCharacters = false;
        private String filename;

        public Builder countBytes(boolean countBytes) {
            this.countBytes = countBytes;
            return this;
        }

        public Builder countLines(boolean countLines) {
            this.countLines = countLines;
            return this;
        }

        public Builder countWords(boolean countWords) {
            this.countWords = countWords;
            return this;
        }

        public Builder countCharacters(boolean countCharacters) {
            this.countCharacters = countCharacters;
            return this;
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public CcwcOptions build() {
            return new CcwcOptions(this);
        }
    }

    @Override
    public String toString() {
        return "CcwcOptions{" + "countBytes=" + countBytes + ", countLines=" + countLines
                + ", countWords=" + countWords + ", countCharacters=" + countCharacters
                + ", filename='" + filename + '\'' + '}';
    }
}
