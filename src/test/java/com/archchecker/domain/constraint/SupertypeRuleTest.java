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

class SupertypeRuleTest {

    @Test
    void classWithRequiredSupertypeIsCompliant(@TempDir Path tmp) throws IOException {
        Path src = writeJava(tmp, "DerivedRule",
                "package com.foo.domain.constraint;\n"
                + "class DerivedRule extends ArchitectureConstraint {}\n");
        SupertypeRule rule = new SupertypeRule("R-SUP-01",
                "constraint must extend ArchitectureConstraint",
                "com.foo.domain.constraint", "ArchitectureConstraint");

        List<Violation> result = rule.validate(
                List.of(new File(src, "com.foo.domain.constraint")));

        assertTrue(result.isEmpty());
    }

    @Test
    void classMissingRequiredSupertypeReportsViolation(@TempDir Path tmp) throws IOException {
        Path src = writeJava(tmp, "BadRule",
                "package com.foo.domain.constraint;\nclass BadRule {}\n");
        SupertypeRule rule = new SupertypeRule("R-SUP-01",
                "constraint must extend ArchitectureConstraint",
                "com.foo.domain.constraint", "ArchitectureConstraint");

        List<Violation> result = rule.validate(
                List.of(new File(src, "com.foo.domain.constraint")));

        assertEquals(1, result.size());
        assertTrue(result.get(0).getMessage().contains("ArchitectureConstraint"));
    }

    private static Path writeJava(Path dir, String name, String body) throws IOException {
        Path p = dir.resolve(name + ".java");
        Files.writeString(p, body);
        return p;
    }
}
