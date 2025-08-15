package com.codingchallenge.ccwc.service.impl;

import com.codingchallenge.ccwc.cli.CcwcOptions;
import com.codingchallenge.ccwc.exception.FileOperationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileAnalysisServiceImpl.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class FileAnalysisServiceImplTest {

    private final FileAnalysisServiceImpl service = new FileAnalysisServiceImpl();

    @Test
    public void testByteCountWithExistingFile(@TempDir Path tempDir) throws Exception {
        // Create a test file with known content
        Path testFile = tempDir.resolve("test.txt");
        String content = "Hello, World!";
        Files.write(testFile, content.getBytes());

        CcwcOptions options =
                CcwcOptions.builder().countBytes(true).filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show byte count and filename
        assertTrue(result.contains(String.valueOf(content.getBytes().length)));
        assertTrue(result.contains(testFile.getFileName().toString()));
    }

    @Test
    public void testFileNotFound() {
        CcwcOptions options =
                CcwcOptions.builder().countBytes(true).filename("/non/existent/file.txt").build();

        assertThrows(FileOperationException.class, () -> service.analyze(options));
    }

    @Test
    public void testEmptyFile(@TempDir Path tempDir) throws Exception {
        // Create an empty test file
        Path testFile = tempDir.resolve("empty.txt");
        Files.write(testFile, new byte[0]);

        CcwcOptions options =
                CcwcOptions.builder().countBytes(true).filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show 0 bytes
        assertTrue(result.contains("0"));
    }

    @Test
    public void testLineCountWithKnownContent(@TempDir Path tempDir) throws Exception {
        // Create a test file with known line content
        Path testFile = tempDir.resolve("lines_test.txt");
        String content = "line1\nline2\nline3\n";
        Files.write(testFile, content.getBytes());

        CcwcOptions options =
                CcwcOptions.builder().countLines(true).filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show 3 lines (3 newline characters)
        assertTrue(result.contains("3"));
        assertTrue(result.contains(testFile.getFileName().toString()));
    }

    @Test
    public void testLineCountWithNoNewline(@TempDir Path tempDir) throws Exception {
        // Create a test file with no trailing newline
        Path testFile = tempDir.resolve("no_newline_test.txt");
        String content = "single line no newline";
        Files.write(testFile, content.getBytes());

        CcwcOptions options =
                CcwcOptions.builder().countLines(true).filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show 0 lines (no newline characters, matching Unix wc behavior)
        assertTrue(result.contains("0"));
    }

    @Test
    public void testWordCountWithKnownContent(@TempDir Path tempDir) throws Exception {
        // Create a test file with known word content
        Path testFile = tempDir.resolve("words_test.txt");
        String content = "one two three four five";
        Files.write(testFile, content.getBytes());

        CcwcOptions options =
                CcwcOptions.builder().countWords(true).filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show 5 words
        assertTrue(result.contains("5"));
        assertTrue(result.contains(testFile.getFileName().toString()));
    }

    @Test
    public void testWordCountWithExtraWhitespace(@TempDir Path tempDir) throws Exception {
        // Create a test file with extra whitespace
        Path testFile = tempDir.resolve("whitespace_test.txt");
        String content = "  word1   word2    word3  ";
        Files.write(testFile, content.getBytes());

        CcwcOptions options =
                CcwcOptions.builder().countWords(true).filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show 3 words (whitespace trimmed and split correctly)
        assertTrue(result.contains("3"));
    }

    @Test
    public void testWordCountEmptyFile(@TempDir Path tempDir) throws Exception {
        // Create an empty test file
        Path testFile = tempDir.resolve("empty_words.txt");
        Files.write(testFile, new byte[0]);

        CcwcOptions options =
                CcwcOptions.builder().countWords(true).filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show 0 words
        assertTrue(result.contains("0"));
    }

    @Test
    public void testCharacterCountWithMultibyte(@TempDir Path tempDir) throws Exception {
        // Create a test file with multibyte characters
        Path testFile = tempDir.resolve("multibyte_test.txt");
        String content = "Hello 🌍!"; // 9 Unicode code points
        Files.write(testFile, content.getBytes(StandardCharsets.UTF_8));

        CcwcOptions options =
                CcwcOptions.builder().countCharacters(true).filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show 9 characters (Unicode code points, not UTF-16 code units)
        assertTrue(result.contains("9"));
        assertTrue(result.contains(testFile.getFileName().toString()));
    }

    @Test
    public void testCharacterVsByteCountMutualExclusion(@TempDir Path tempDir) throws Exception {
        // Test that -c takes precedence over -m when both are specified
        Path testFile = tempDir.resolve("exclusion_test.txt");
        String content = "Hello 🌍!"; // 9 chars, more bytes due to UTF-8 encoding
        Files.write(testFile, content.getBytes());

        CcwcOptions options = CcwcOptions.builder().countCharacters(true).countBytes(true) // Both
                                                                                           // specified,
                                                                                           // bytes
                                                                                           // should
                                                                                           // take
                                                                                           // precedence
                .filename(testFile.toString()).build();

        String result = service.analyze(options);

        // Should show byte count, not character count (Unix wc behavior)
        int expectedByteCount = content.getBytes().length;
        assertTrue(result.contains(String.valueOf(expectedByteCount)));

        // Extract the number to verify it's the byte count, not character count
        String[] parts = result.trim().split("\\s+");
        assertEquals(expectedByteCount, Integer.parseInt(parts[0]));
    }

    @Test
    public void testDefaultBehavior(@TempDir Path tempDir) throws Exception {
        // Test default behavior (no options) should be equivalent to -l -w -c
        Path testFile = tempDir.resolve("default_test.txt");
        String content = "line1\nline2\nline3\n";
        Files.write(testFile, content.getBytes());

        CcwcOptions defaultOptions = CcwcOptions.builder().filename(testFile.toString()).build(); // No
                                                                                                  // options
                                                                                                  // set,
                                                                                                  // should
                                                                                                  // use
                                                                                                  // default
                                                                                                  // mode

        String result = service.analyze(defaultOptions);

        // Should show lines, words, bytes in that order
        String[] parts = result.trim().split("\\s+");
        assertEquals(4, parts.length); // 3 counts + filename

        assertEquals("3", parts[0]); // 3 lines
        assertEquals("3", parts[1]); // 3 words
        assertEquals(String.valueOf(content.getBytes().length), parts[2]); // byte count
        assertTrue(parts[3].contains(testFile.getFileName().toString())); // filename
    }

    @Test
    public void testDefaultBehaviorEmptyFile(@TempDir Path tempDir) throws Exception {
        // Test default behavior with empty file
        Path testFile = tempDir.resolve("empty_default.txt");
        Files.write(testFile, new byte[0]);

        CcwcOptions defaultOptions = CcwcOptions.builder().filename(testFile.toString()).build();

        String result = service.analyze(defaultOptions);

        // Should show all zeros
        assertTrue(result.contains("0       0       0"));
    }

    @Test
    public void testStdinSupport(@TempDir Path tempDir) throws Exception {
        // Test stdin functionality by simulating stdin input
        CcwcOptions stdinOptions = CcwcOptions.builder().countLines(true).filename(null) // null
                                                                                         // filename
                                                                                         // indicates
                                                                                         // stdin
                .build();

        // Note: This test verifies that the option parsing works correctly for stdin
        // The actual stdin reading is tested through integration tests
        assertTrue(stdinOptions.isReadingFromStdin());
        assertEquals(null, stdinOptions.getFilename());
    }
}
