# CCWC - Coding Challenge Word Count

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A Java implementation of the Unix `wc` (word count) command line tool, built following enterprise coding standards with a highly modular and extensible architecture.

> **Note**: This is a solution to the [Coding Challenges - Write Your Own wc Tool](https://codingchallenges.fyi/challenges/challenge-wc) challenge.

## Overview

This project implements the Unix Philosophy principles:

- **Writing simple parts connected by clean interfaces** - Each component does one thing well with a simple, clean API
- **Design programs to be connected to other programs** - Built to be easily integrated with other tools and systems

## Architecture

The project follows enterprise Java standards with clear separation of concerns:

```
com.codingchallenge.ccwc/
├── CcwcApplication           # Main application entry point
├── cli/
│   ├── CcwcOptions          # Command line options data model (Builder pattern)
│   └── CommandLineParser    # Command line argument parser
├── service/
│   ├── FileAnalysisService  # Service interface (Strategy pattern)
│   └── impl/
│       └── FileAnalysisServiceImpl  # Default file analysis implementation
├── formatter/
│   ├── OutputFormatter      # Output formatting interface
│   └── impl/
│       └── DefaultOutputFormatter   # Default output formatter
└── exception/
    ├── CcwcException        # Base exception class
    ├── InvalidArgumentException     # Invalid arguments exception
    └── FileOperationException       # File operation exception
```

## Features

### Step One - Byte Count (-c option)

✅ **Implemented**: Count the number of bytes in a file

```bash
./ccwc -c test.txt
  405783 test.txt
```

### Step Two - Line Count (-l option)

✅ **Implemented**: Count the number of lines in a file

```bash
./ccwc -l test.txt
    8894 test.txt
```

### Step Three - Word Count (-w option)

✅ **Implemented**: Count the number of words in a file

```bash
./ccwc -w test.txt
   70826 test.txt
```

### Step Four - Character Count (-m option)

✅ **Implemented**: Count the number of characters (Unicode code points) in a file

```bash
./ccwc -m test.txt
  392888 test.txt
```

### Step Five - Default Behavior (no options)

✅ **Implemented**: Default behavior equivalent to `-l -w -c` (lines, words, bytes)

```bash
./ccwc test.txt
    8894   70826  405783 test.txt
```

### The Final Step - Standard Input Support

✅ **Implemented**: Read from stdin when no filename provided (Unix pipe support)

```bash
cat test.txt | ./ccwc -l
    8894

echo "hello world" | ./ccwc
       1       2      12
```

### Multiple Options Support

✅ **Implemented**: Support for multiple options together (follows Unix wc behavior)

```bash
./ccwc -l -w -c test.txt
    8894   70826  405783 test.txt

# Note: -c and -m are mutually exclusive (byte count takes precedence)
./ccwc -l -w -m -c test.txt
    8894   70826  405783 test.txt
```

### Future Steps (Extensible Design)

The modular architecture supports easy extension for:

- Line count (-l option)
- Word count (-w option)
- Character count (-m option)
- Multiple files support
- Standard input support
- Alternative output formats (JSON, CSV, etc.)

## Build Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package
```

## Usage

### Option 1: Using the JAR directly

```bash
# With file
java -jar target/ccwc-1.0.0.jar test.txt

# With stdin
cat test.txt | java -jar target/ccwc-1.0.0.jar -l
```

### Option 2: Using the convenience script

```bash
# With file
./ccwc test.txt

# With stdin (Unix pipe support)
cat test.txt | ./ccwc -l
```

## Supported Options

| Option | Description                            | Status         |
| ------ | -------------------------------------- | -------------- |
| `-c`   | Count bytes                            | ✅ Implemented |
| `-l`   | Count lines                            | ✅ Implemented |
| `-w`   | Count words                            | ✅ Implemented |
| `-m`   | Count characters (Unicode code points) | ✅ Implemented |

## Testing

The project includes comprehensive unit tests:

```bash
mvn test
```

Test coverage includes:

- File analysis functionality
- Error handling (file not found, empty files)
- Edge cases

## Design Patterns Used

1. **Builder Pattern** - For constructing CcwcOptions objects
2. **Strategy Pattern** - FileAnalysisService interface for different analysis strategies
3. **Dependency Injection** - Constructor injection for testability
4. **Template Method** - Extensible formatting system

## Error Handling

The application provides clear error messages for common scenarios:

- File not found
- Invalid command line arguments
- File access permissions
- Unexpected errors

## Performance

- Efficient file reading using `Files.readAllBytes()`
- Minimal memory footprint
- Fast startup time with Maven Shade Plugin

## Contributing

The codebase follows enterprise Java standards:

- Clear package structure
- Comprehensive JavaDoc
- Unit test coverage
- Clean separation of concerns
- SOLID principles

## Implementation Highlights

### Key Challenges Solved

1. **Unicode Character Counting**: Proper handling of multibyte characters using Unicode code points instead of UTF-16 code units
2. **Unix Compatibility**: Exact matching with system `wc` behavior, including newline counting and option precedence
3. **Standard Input Support**: Full pipe support for Unix-style command chaining
4. **Mutual Exclusion**: Proper handling of `-c` and `-m` options (byte count takes precedence)

### Architecture Benefits

- **Testability**: 17 comprehensive unit tests covering all functionality and edge cases
- **Extensibility**: Easy to add new counting methods or output formats
- **Maintainability**: Clear separation of concerns following SOLID principles
- **Performance**: Efficient file processing with minimal memory usage

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Part of the [Coding Challenges](https://codingchallenges.fyi/) series
- Inspired by the Unix `wc` command and Unix Philosophy principles
