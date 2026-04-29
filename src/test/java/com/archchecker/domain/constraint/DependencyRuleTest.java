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

class DependencyRuleTest {

    @Test
    void disallowedImportProducesViolation(@TempDir Path tmp) throws IOException {
        Path src = writeJava(tmp, "C",
                "package com.foo.domain;\nimport com.foo.infra.X;\nclass C {}\n");
        DependencyRule rule = new DependencyRule("R-DEP-01",
                "domain must not depend on infra",
                "com.foo.domain", "com.foo.infra", false);

        List<Violation> result = rule.validate(List.of(new File(src, "com.foo.domain")));

        assertEquals(1, result.size());
        assertTrue(result.get(0).getMessage().contains("must not depend on"));
    }

    @Test
    void allowedImportsAreIgnored(@TempDir Path tmp) throws IOException {
        Path src = writeJava(tmp, "C",
                "package com.foo.app;\nimport com.foo.domain.Y;\nclass C {}\n");
        DependencyRule rule = new DependencyRule("R-DEP-02",
                "app must depend on domain",
                "com.foo.app", "com.foo.domain", true);

        List<Violation> result = rule.validate(List.of(new File(src, "com.foo.app")));

        assertTrue(result.isEmpty(), "expected no violation but got " + result);
    }

    @Test
    void filesOutsideSourcePackageAreSkipped(@TempDir Path tmp) throws IOException {
        Path src = writeJava(tmp, "C",
                "package com.bar;\nimport com.foo.infra.X;\nclass C {}\n");
        DependencyRule rule = new DependencyRule("R-DEP-01",
                "domain must not depend on infra",
                "com.foo.domain", "com.foo.infra", false);

        List<Violation> result = rule.validate(List.of(new File(src, "com.bar")));

        assertTrue(result.isEmpty());
    }

    private static Path writeJava(Path dir, String name, String body) throws IOException {
        Path p = dir.resolve(name + ".java");
        Files.writeString(p, body);
        return p;
    }
}
