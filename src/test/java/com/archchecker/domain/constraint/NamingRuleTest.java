package com.archchecker.domain.constraint;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Violation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NamingRuleTest {

    @Test
    void compliantClassNameProducesNoViolation(@TempDir Path tmp) throws IOException {
        Path src = writeJava(tmp, "AccountService",
                "package com.foo;\nclass AccountService {}\n");
        NamingRule rule = new NamingRule("R-NAME-01", "Service suffix", ".*Service");

        List<Violation> result = rule.validate(List.of(new File(src, "com.foo")));

        assertTrue(result.isEmpty(), "expected no violation but got " + result);
    }

    @Test
    void nonCompliantClassNameProducesOneViolation(@TempDir Path tmp) throws IOException {
        Path src = writeJava(tmp, "AccountManager",
                "package com.foo;\nclass AccountManager {}\n");
        NamingRule rule = new NamingRule("R-NAME-01", "Service suffix", ".*Service");

        List<Violation> result = rule.validate(List.of(new File(src, "com.foo")));

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getLineNumber());
        assertTrue(result.get(0).getMessage().contains("AccountManager"));
    }

    @Test
    void multipleClassesInOneFileAreEachChecked(@TempDir Path tmp) throws IOException {
        Path src = writeJava(tmp, "Mixed",
                "package com.foo;\nclass GoodService {}\nclass BadName {}\n");
        NamingRule rule = new NamingRule("R-NAME-01", "Service suffix", ".*Service");

        List<Violation> result = rule.validate(List.of(new File(src, "com.foo")));

        assertEquals(1, result.size());
        assertTrue(result.get(0).getMessage().contains("BadName"));
    }

    private static Path writeJava(Path dir, String name, String body) throws IOException {
        Path p = dir.resolve(name + ".java");
        Files.writeString(p, body);
        return p;
    }
}
