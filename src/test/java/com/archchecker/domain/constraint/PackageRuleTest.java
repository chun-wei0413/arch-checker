package com.archchecker.domain.constraint;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Violation;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PackageRuleTest {

    @Test
    void presentPackagePatternIsCompliant() {
        File f = new File(Paths.get("X.java"), "com.foo.domain.constraint");
        PackageRule rule = new PackageRule("R-PKG-01",
                "domain layer must exist", "com.foo.domain.**");

        List<Violation> result = rule.validate(List.of(f));

        assertTrue(result.isEmpty());
    }

    @Test
    void missingPackagePatternReportsViolation() {
        File f = new File(Paths.get("X.java"), "com.bar");
        PackageRule rule = new PackageRule("R-PKG-01",
                "domain layer must exist", "com.foo.domain.**");

        List<Violation> result = rule.validate(List.of(f));

        assertEquals(1, result.size());
        assertTrue(result.get(0).getMessage().contains("com.foo.domain.**"));
    }
}
