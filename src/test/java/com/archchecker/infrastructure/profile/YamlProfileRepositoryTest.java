package com.archchecker.infrastructure.profile;

import com.archchecker.domain.constraint.ArchitectureConstraint;
import com.archchecker.domain.constraint.DependencyRule;
import com.archchecker.domain.constraint.NamingRule;
import com.archchecker.domain.profile.StyleProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class YamlProfileRepositoryTest {

    @Test
    void loadsNamingAndDependencyRules(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("arch.yaml");
        Files.writeString(file,
                "name: demo\n"
                + "rules:\n"
                + "  - id: R-NAME-01\n"
                + "    type: naming\n"
                + "    description: Service suffix\n"
                + "    classNamePattern: .*Service\n"
                + "  - id: R-DEP-01\n"
                + "    type: dependency\n"
                + "    description: domain isolation\n"
                + "    sourcePackage: com.foo.domain\n"
                + "    targetPackage: com.foo.infra\n"
                + "    isAllowed: false\n");
        YamlProfileRepository repo = new YamlProfileRepository();

        StyleProfile profile = repo.load(file);

        assertEquals("demo", profile.getName());
        assertEquals(2, profile.getConstraints().size());
        ArchitectureConstraint first = profile.getConstraints().get(0);
        assertTrue(first instanceof NamingRule);
        assertEquals("R-NAME-01", first.getId());
        ArchitectureConstraint second = profile.getConstraints().get(1);
        assertTrue(second instanceof DependencyRule);
        assertFalse(((DependencyRule) second).isAllowed());
    }

    @Test
    void unknownRuleTypeIsRejected(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("arch.yaml");
        Files.writeString(file,
                "name: x\n"
                + "rules:\n"
                + "  - id: r\n"
                + "    type: bogus\n");
        YamlProfileRepository repo = new YamlProfileRepository();

        assertThrows(IllegalArgumentException.class, () -> repo.load(file));
    }

    @Test
    void missingProfileFileIsRejected(@TempDir Path tmp) {
        Path file = tmp.resolve("absent.yaml");
        YamlProfileRepository repo = new YamlProfileRepository();

        assertThrows(IllegalArgumentException.class, () -> repo.load(file));
    }
}
