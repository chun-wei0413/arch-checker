package com.archchecker.application;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.compliance.Violation;
import com.archchecker.domain.constraint.ArchitectureConstraint;
import com.archchecker.domain.profile.StyleProfile;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SuppressionServiceTest {

    @Test
    void suppressAppendsAndPersistsRecord() {
        StubRule rule = new StubRule("R-NAME-01");
        FakeProfileRepo profileRepo = new FakeProfileRepo(
                new StyleProfile("p", List.of(rule)));
        SpyRepo repo = new SpyRepo();
        SuppressionService service = new SuppressionService(repo, profileRepo);

        Suppression s = service.suppress(
                Paths.get("/p.yaml"), Paths.get("/s.yaml"),
                "R-NAME-01", Paths.get("/x/A.java"), 7, "legacy code");

        assertNotNull(s.getTimestamp(), "timestamp must be set");
        assertEquals(1, repo.savedRecords.size());
        assertEquals("R-NAME-01", repo.savedRecords.get(0).getConstraint().getId());
    }

    @Test
    void suppressUnknownConstraintIdThrows() {
        FakeProfileRepo profileRepo = new FakeProfileRepo(
                new StyleProfile("p", Collections.emptyList()));
        SuppressionService service = new SuppressionService(new SpyRepo(), profileRepo);

        assertThrows(IllegalArgumentException.class, () ->
                service.suppress(Paths.get("/p.yaml"), Paths.get("/s.yaml"),
                        "missing", Paths.get("/x/A.java"), 1, "no"));
    }

    private static class StubRule extends ArchitectureConstraint {
        StubRule(String id) { super(id, "stub"); }
        @Override public List<Violation> validate(List<File> files) {
            return Collections.emptyList();
        }
    }

    private static class FakeProfileRepo implements ProfileRepository {
        private final StyleProfile profile;
        FakeProfileRepo(StyleProfile p) { this.profile = p; }
        @Override public StyleProfile load(Path profilePath) { return profile; }
    }

    private static class SpyRepo implements SuppressionRepository {
        private final List<Suppression> initial = new ArrayList<>();
        List<Suppression> savedRecords = new ArrayList<>();
        @Override public List<Suppression> loadAll(Path f, StyleProfile p) { return initial; }
        @Override public void save(Path f, List<Suppression> s) {
            savedRecords = new ArrayList<>(s);
        }
    }
}
