package com.codingchallenge.ccwc.service.impl;

import com.codingchallenge.ccwc.cli.CcwcOptions;
import com.codingchallenge.ccwc.exception.CcwcException;
import com.codingchallenge.ccwc.exception.FileOperationException;
import com.codingchallenge.ccwc.service.FileAnalysisService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Default implementation of FileAnalysisService. Provides file analysis functionality including
 * byte count, line count, word count, and character count.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class FileAnalysisServiceImpl implements FileAnalysisService {

    @Override
    public String analyze(CcwcOptions options) throws CcwcException {
        try {
            FileStats stats;
            if (options.isReadingFromStdin()) {
                stats = calculateStatsFromStdin();
            } else {
                Path filePath = Paths.get(options.getFilename());

                if (!Files.exists(filePath)) {
                    throw new FileOperationException("File not found: " + options.getFilename());
                }

                if (!Files.isRegularFile(filePath)) {
                    throw new FileOperationException(
                            "Not a regular file: " + options.getFilename());
                }

                stats = calculateStats(filePath);
            }
            return formatResult(stats, options);
        } catch (IOException e) {
            String source = options.isReadingFromStdin() ? "standard input" : options.getFilename();
            throw new FileOperationException("Failed to read from " + source, e);
        }
    }

    /**
     * Calculates file statistics.
     * 
     * @param filePath the path to the file
     * @return file statistics
     * @throws IOException if file reading fails
     */
    private FileStats calculateStats(Path filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(filePath);
        String content = new String(bytes);

        long byteCount = bytes.length;
        // Count newline characters to match Unix wc behavior
        long lineCount = content.chars().filter(ch -> ch == '\n').count();
        long wordCount = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;
        // Count Unicode code points, not UTF-16 code units
        long charCount = content.codePointCount(0, content.length());

        return new FileStats(byteCount, lineCount, wordCount, charCount);
    }

    /**
     * Calculates file statistics from standard input.
     * 
     * @return file statistics
     * @throws IOException if reading from stdin fails
     */
    private FileStats calculateStatsFromStdin() throws IOException {
        byte[] bytes = System.in.readAllBytes();
        String content = new String(bytes);

        long byteCount = bytes.length;
        // Count newline characters to match Unix wc behavior
        long lineCount = content.chars().filter(ch -> ch == '\n').count();
        long wordCount = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;
        // Count Unicode code points, not UTF-16 code units
        long charCount = content.codePointCount(0, content.length());

        return new FileStats(byteCount, lineCount, wordCount, charCount);
    }

    /**
     * Formats the result based on the options.
     * 
     * @param stats the file statistics
     * @param options the options
     * @return formatted result
     */
    private String formatResult(FileStats stats, CcwcOptions options) {
        StringBuilder result = new StringBuilder();

        // Output in the same order as Unix wc: lines, words, chars, bytes
        if (options.isCountLines()) {
            result.append(String.format("%8d", stats.getLineCount()));
        }

        if (options.isCountWords()) {
            result.append(String.format("%8d", stats.getWordCount()));
        }

        // Unix wc behavior: -c and -m are mutually exclusive, -c takes precedence
        if (options.isCountBytes()) {
            result.append(String.format("%8d", stats.getByteCount()));
        } else if (options.isCountCharacters()) {
            result.append(String.format("%8d", stats.getCharCount()));
        }

        // If no specific options are set, show all counts (default behavior)
        if (options.isDefaultMode()) {
            result.append(String.format("%8d", stats.getLineCount()));
            result.append(String.format("%8d", stats.getWordCount()));
            result.append(String.format("%8d", stats.getByteCount()));
        }

        // Only append filename if not reading from stdin
        if (!options.isReadingFromStdin()) {
            result.append(" ").append(options.getFilename());
        }

        return result.toString();
    }

    /**
     * Inner class to hold file statistics.
     */
    private static class FileStats {
        private final long byteCount;
        private final long lineCount;
        private final long wordCount;
        private final long charCount;

        public FileStats(long byteCount, long lineCount, long wordCount, long charCount) {
            this.byteCount = byteCount;
            this.lineCount = lineCount;
            this.wordCount = wordCount;
            this.charCount = charCount;
        }

        public long getByteCount() {
            return byteCount;
        }

        public long getLineCount() {
            return lineCount;
        }

        public long getWordCount() {
            return wordCount;
        }

        public long getCharCount() {
            return charCount;
        }
    }
}
