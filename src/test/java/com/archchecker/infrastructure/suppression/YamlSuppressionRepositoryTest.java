package com.archchecker.infrastructure.suppression;

import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.constraint.NamingRule;
import com.archchecker.domain.profile.StyleProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class YamlSuppressionRepositoryTest {

    @Test
    void roundTripPreservesFields(@TempDir Path tmp) throws IOException {
        StyleProfile profile = new StyleProfile("p",
                List.of(new NamingRule("R-NAME-01", "service suffix", ".*Service")));
        Suppression sup = new Suppression(profile.getConstraints().get(0),
                Paths.get("src/main/java/A.java"), 12, "legacy",
                Instant.parse("2026-04-29T10:00:00Z"));
        Path file = tmp.resolve(".arch-checker-suppress.yaml");
        YamlSuppressionRepository repo = new YamlSuppressionRepository();

        repo.save(file, List.of(sup));
        List<Suppression> loaded = repo.loadAll(file, profile);

        assertEquals(1, loaded.size());
        Suppression r = loaded.get(0);
        assertEquals("R-NAME-01", r.getConstraint().getId());
        assertEquals(12, r.getLineNumber());
        assertEquals("legacy", r.getReason());
        assertEquals(Instant.parse("2026-04-29T10:00:00Z"), r.getTimestamp());
    }

    @Test
    void missingFileReturnsEmptyList(@TempDir Path tmp) {
        StyleProfile profile = new StyleProfile("p",
                List.of(new NamingRule("R-NAME-01", "x", ".*")));
        YamlSuppressionRepository repo = new YamlSuppressionRepository();

        List<Suppression> loaded = repo.loadAll(tmp.resolve("absent.yaml"), profile);

        assertTrue(loaded.isEmpty());
    }

    @Test
    void recordsReferencingUnknownIdAreSkipped(@TempDir Path tmp) throws IOException {
        StyleProfile profile = new StyleProfile("p",
                List.of(new NamingRule("R-NAME-01", "x", ".*")));
        Path file = tmp.resolve(".arch-checker-suppress.yaml");
        Files.writeString(file,
                "- constraintId: R-MISSING\n"
                + "  filePath: src/A.java\n"
                + "  lineNumber: 1\n"
                + "  reason: \"obsolete\"\n"
                + "  timestamp: 2026-04-29T10:00:00Z\n");
        YamlSuppressionRepository repo = new YamlSuppressionRepository();

        assertTrue(repo.loadAll(file, profile).isEmpty());
    }
}
