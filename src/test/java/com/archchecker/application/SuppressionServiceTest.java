package com.archchecker.application;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.compliance.Violation;
import com.archchecker.domain.rule.ComplianceRule;
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
        FakeProfileLoader profileLoader = new FakeProfileLoader(
                new StyleProfile("p", List.of(rule)));
        SpyStore store = new SpyStore();
        SuppressionService service = new SuppressionService(store, profileLoader);

        Suppression s = service.suppress(
                Paths.get("/p.yaml"), Paths.get("/s.yaml"),
                "R-NAME-01", Paths.get("/x/A.java"), 7, "legacy code");

        assertNotNull(s.getTimestamp(), "timestamp must be set");
        assertEquals(1, store.savedRecords.size());
        assertEquals("R-NAME-01", store.savedRecords.get(0).getRule().getId());
    }

    @Test
    void suppressUnknownConstraintIdThrows() {
        FakeProfileLoader profileLoader = new FakeProfileLoader(
                new StyleProfile("p", Collections.emptyList()));
        SuppressionService service = new SuppressionService(new SpyStore(), profileLoader);

        assertThrows(IllegalArgumentException.class, () ->
                service.suppress(Paths.get("/p.yaml"), Paths.get("/s.yaml"),
                        "missing", Paths.get("/x/A.java"), 1, "no"));
    }

    private static class StubRule extends ComplianceRule {
        StubRule(String id) { super(id, "stub"); }
        @Override public List<Violation> validate(List<File> files) {
            return Collections.emptyList();
        }
    }

    private static class FakeProfileLoader implements ProfileLoader {
        private final StyleProfile profile;
        FakeProfileLoader(StyleProfile p) { this.profile = p; }
        @Override public StyleProfile load(Path profilePath) { return profile; }
    }

    private static class SpyStore implements SuppressionStore {
        private final List<Suppression> initial = new ArrayList<>();
        List<Suppression> savedRecords = new ArrayList<>();
        @Override public List<Suppression> loadAll(Path f, StyleProfile p) { return initial; }
        @Override public void save(Path f, List<Suppression> s) {
            savedRecords = new ArrayList<>(s);
        }
    }
}
