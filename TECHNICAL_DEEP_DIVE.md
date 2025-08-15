# Technical Deep Dive: Complex Implementation Details

## 🧠 Unicode Character Counting - The Most Complex Challenge

### The Problem
Creating a `wc -m` equivalent that counts Unicode **code points** (not UTF-16 code units) while maintaining exact compatibility with Unix `wc`.

### Why This Is Complex
```java
// WRONG APPROACH - Counts UTF-16 code units
String text = "Hello 🌍!";
int wrong = text.length(); // Returns 10 (emoji uses 2 code units)

// CORRECT APPROACH - Counts Unicode code points  
int correct = text.codePointCount(0, text.length()); // Returns 9
```

### Real-World Example
| Character | UTF-16 Code Units | Unicode Code Points | What `wc -m` Shows |
|-----------|------------------|-------------------|------------------|
| `H` | 1 | 1 | 1 |
| `🌍` | 2 | 1 | 1 |
| `👨‍💻` | 5 | 1 | 1 |

### Implementation Deep Dive
```java
private long countCharacters(String content) {
    // This method handles the most complex Unicode scenarios
    return content.codePointCount(0, content.length());
}

// Why not just content.length()?
// Example: "👨‍💻" (man technologist emoji)
// - content.length() = 5 (UTF-16 code units)
// - codePointCount() = 1 (actual character)
// - Unix wc -m shows: 1
```

---

## 🔄 Option Precedence Logic - Unix Compatibility Nightmare

### The Challenge
Unix `wc` has complex rules for when multiple options are specified:

```bash
wc -c -m file.txt    # -c wins (shows bytes, not characters)
wc -m -c file.txt    # -c wins (order doesn't matter)
wc -l -w -c file.txt # Shows all three
wc file.txt          # Same as -l -w -c
```

### Implementation Strategy
```java
// Order of evaluation matters for precedence
if (options.isCountBytes()) {
    // Bytes always win over characters
    stats.setByteCount(Files.size(path));
} else if (options.isCountCharacters()) {
    // Characters only if bytes not requested
    stats.setCharacterCount(countCharacters(content));
}

// But lines and words can coexist with either
if (options.isCountLines()) {
    stats.setLineCount(countLines(content));
}
if (options.isCountWords()) {
    stats.setWordCount(countWords(content));
}
```

### Why This Complexity Exists
- **Historical Unix Behavior**: `wc` evolved over decades
- **Performance Optimization**: Bytes can be counted without reading file content
- **User Expectations**: Developers expect exact `wc` compatibility

---

## 🚰 Stdin Detection - The Hidden Complexity

### Multiple Stdin Scenarios
```java
// Scenario 1: No arguments (default to line count from stdin)
wc                    # Reads stdin, shows lines

// Scenario 2: Option only (specified count from stdin)  
wc -c                 # Reads stdin, shows bytes
wc -l -w              # Reads stdin, shows lines and words

// Scenario 3: Filename provided (read from file)
wc file.txt           # Reads file, shows lines/words/bytes
```

### Detection Algorithm
```java
public CcwcOptions parse(String[] args) {
    // Complex stdin detection logic
    if (args.length == 0) {
        // Case 1: `wc` with no arguments
        return CcwcOptions.builder()
            .filename(null)          // null = stdin
            .countLines(true)        // Default for stdin
            .build();
    }
    
    if (args.length == 1 && args[0].startsWith("-")) {
        // Case 2: `wc -l` (option only, no filename)
        return parseOptions(args)
            .filename(null)          // null = stdin
            .build();
    }
    
    // Case 3: Has filename, read from file
    // ... regular file processing
}
```

### Why Null Filename Is Elegant
- **Type Safety**: `String filename` can be null or actual filename
- **Service Layer**: Single method handles both stdin and file input
- **Unix Philosophy**: "Everything is a file" - stdin is just a special file

---

## 🧪 Test Design Philosophy - Edge Case Mastery

### The Multibyte Character Test (Our Recent Fix)
```java
@Test
public void testCharacterCountWithMultibyte(@TempDir Path tempDir) throws Exception {
    Path testFile = tempDir.resolve("multibyte_test.txt");
    String content = "Hello 🌍!"; // 9 Unicode code points
    
    // CRITICAL: Explicit UTF-8 encoding
    Files.write(testFile, content.getBytes(StandardCharsets.UTF_8));
    
    CcwcOptions options = CcwcOptions.builder()
        .countCharacters(true)
        .filename(testFile.toString())
        .build();
    
    String result = service.analyze(options);
    
    // Verify exactly 9 code points (not 10 or 11)
    assertTrue(result.contains("9"));
}
```

### Why This Test Is Critical
1. **Cross-Platform**: Ensures UTF-8 on Windows/Linux/macOS
2. **CI/CD**: Catches encoding differences in GitHub Actions
3. **Real-World**: Emoji are common in modern applications
4. **Regression**: Prevents future encoding bugs

### Edge Cases Covered
```java
// Empty file handling
testEmptyFile() // 0 bytes, 0 lines, 0 words, 0 characters

// Whitespace-only files  
testFileWithOnlyWhitespace() // bytes > 0, but 0 words

// Mixed Unicode content
"ASCII text with 🌍 emoji and ñ accents" // Complex character counting

// Stdin edge cases
cat /dev/null | wc -l     // Empty stdin
echo -n "no newline" | wc # No trailing newline
```

---

## 🏗️ Enterprise Architecture Patterns

### 1. Builder Pattern Implementation
```java
// Fluent API design
CcwcOptions options = CcwcOptions.builder()
    .countLines(true)
    .countWords(true)
    .countBytes(true)
    .filename("test.txt")
    .build();
```

**Why Builder Pattern:**
- **Immutability**: Options can't be changed after creation
- **Readability**: Self-documenting code
- **Validation**: Build-time validation of option combinations
- **Enterprise Standard**: Common in Spring, Maven, etc.

### 2. Dependency Injection (Manual)
```java
public class CcwcApplication {
    private final CommandLineParser parser;
    private final FileAnalysisService service;
    private final OutputFormatter formatter;
    
    // Constructor injection
    public CcwcApplication() {
        this.parser = new CommandLineParser();
        this.service = new FileAnalysisServiceImpl();
        this.formatter = new OutputFormatter();
    }
}
```

**Benefits:**
- **Testability**: Easy to mock dependencies
- **Modularity**: Swap implementations without changing main logic
- **Single Responsibility**: Each class has one job

### 3. Service Layer Pattern
```java
public interface FileAnalysisService {
    String analyze(CcwcOptions options) throws FileOperationException;
}

public class FileAnalysisServiceImpl implements FileAnalysisService {
    // Implementation details hidden behind interface
}
```

**Enterprise Justification:**
- **Abstraction**: Business logic separate from I/O
- **Testing**: Mock the service for unit tests
- **Future Extensions**: Add database storage, caching, etc.

---

## 🚀 Maven Build Complexity

### Shade Plugin Configuration
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.5.0</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.codingchallenge.ccwc.CcwcApplication</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Why Shade Plugin:**
- **Fat JAR**: All dependencies bundled into single executable
- **Distribution**: Easy to share without dependency management
- **Production**: Deploy single JAR to servers
- **Unix Philosophy**: Behave like native `wc` command

---

## 🔄 CI/CD Pipeline Deep Dive

### Matrix Strategy Complexity
```yaml
strategy:
  matrix:
    java-version: [11, 17, 21]
    os: [ubuntu-latest]  # Could expand to windows-latest, macos-latest
```

**Why This Matrix:**
- **Java 11**: Most enterprise environments (LTS until 2026)
- **Java 17**: Current LTS (until 2029)
- **Java 21**: Latest LTS (until 2031)

### Functional Testing in CI
```bash
# Create test data
echo "line1" > test_ci.txt
echo "line2" >> test_ci.txt

# Verify each option works
java -jar target/ccwc-1.0.0.jar -c test_ci.txt  # Should show 12 bytes
java -jar target/ccwc-1.0.0.jar -l test_ci.txt  # Should show 2 lines
java -jar target/ccwc-1.0.0.jar -w test_ci.txt  # Should show 2 words

# Verify stdin works
cat test_ci.txt | java -jar target/ccwc-1.0.0.jar -l  # Should show 2
```

**Why Functional Testing:**
- **Integration**: Ensures JAR actually works
- **Regression**: Catches packaging issues
- **Reality Check**: Tests what users will actually run

---

## 🎯 Performance Considerations

### File Reading Strategy
```java
// For small files (most common case)
String content = Files.readString(path, StandardCharsets.UTF_8);

// For large files (could be optimized)
// Stream processing to avoid loading entire file into memory
```

**Trade-offs:**
- **Simplicity**: Current approach is readable and maintainable
- **Memory**: Works fine for typical text files
- **Scalability**: Could optimize for GB-sized files in future

### Stdin Buffering
```java
// Current approach: Read all stdin at once
StringBuilder content = new StringBuilder();
Scanner scanner = new Scanner(System.in);
while (scanner.hasNextLine()) {
    content.append(scanner.nextLine());
    if (scanner.hasNextLine()) {
        content.append("\n");
    }
}
```

**Future Optimization:**
- Stream processing for large stdin inputs
- Lazy evaluation for count-only operations

---

*This technical deep dive explains the most complex decisions and implementations in the codebase, providing insights into enterprise-level software development practices.*
