package com.codingchallenge.ccwc.cli;

import com.codingchallenge.ccwc.exception.InvalidArgumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandLineParser.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class CommandLineParserTest {

    private final CommandLineParser parser = new CommandLineParser();

    @Test
    public void testParseNoArguments() throws Exception {
        // No arguments should result in stdin with default mode
        String[] args = {};

        CcwcOptions options = parser.parse(args);

        assertTrue(options.isReadingFromStdin());
        assertTrue(options.isDefaultMode());
        assertNull(options.getFilename());
    }

    @Test
    public void testParseWithOptionOnly() throws Exception {
        // Option only should result in stdin
        String[] args = {"-l"};

        CcwcOptions options = parser.parse(args);

        assertTrue(options.isReadingFromStdin());
        assertTrue(options.isCountLines());
        assertFalse(options.isDefaultMode());
        assertNull(options.getFilename());
    }

    @Test
    public void testParseWithFilename() throws Exception {
        // Option with filename
        String[] args = {"-c", "test.txt"};

        CcwcOptions options = parser.parse(args);

        assertFalse(options.isReadingFromStdin());
        assertTrue(options.isCountBytes());
        assertEquals("test.txt", options.getFilename());
    }

    @Test
    public void testParseInvalidOption() {
        String[] args = {"-x", "test.txt"};

        assertThrows(InvalidArgumentException.class, () -> parser.parse(args));
    }
}
