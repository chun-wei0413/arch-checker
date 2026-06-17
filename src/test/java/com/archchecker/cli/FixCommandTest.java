package com.archchecker.cli;

import com.archchecker.application.SuppressionService;
import com.archchecker.application.SuppressionStore;
import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.compliance.Violation;
import com.archchecker.domain.compliance.ViolationReport;
import com.archchecker.domain.profile.StyleProfile;
import com.archchecker.domain.rule.ComplianceRule;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FixCommandTest {

    @Test
    void actionYSuppressesViolationAndReturnsExitZero() {
        StubRule rule = new StubRule("R-NAME-01");
        Violation v = new Violation(1, "bad name",
                new File(Paths.get("/x/A.java"), "com.foo"), rule, "Rename it");
        SpyStore store = new SpyStore();
        SuppressionService service = new SuppressionService(store, null);

        int[] counts = fixCommandWith("y\n\n")  // y + empty reason
                .runInteractiveLoop(reportWith(v), profile(rule), service, Paths.get("/s.yaml"));

        assertEquals(1, counts[0], "newlySuppressed");
        assertEquals(0, counts[1], "remaining");
        assertEquals(1, store.saved.size());
        assertEquals("Suppressed interactively", store.saved.get(0).getReason());
    }

    @Test
    void actionYWithCustomReasonStoresReason() {
        StubRule rule = new StubRule("R-NAME-01");
        Violation v = new Violation(1, "bad name",
                new File(Paths.get("/x/A.java"), "com.foo"), rule);
        SpyStore store = new SpyStore();
        SuppressionService service = new SuppressionService(store, null);

        fixCommandWith("y\ncontroller 層允許此依賴\n")
                .runInteractiveLoop(reportWith(v), profile(rule), service, Paths.get("/s.yaml"));

        assertEquals("controller 層允許此依賴", store.saved.get(0).getReason());
    }

    @Test
    void actionNKeepsViolationInRemaining() {
        StubRule rule = new StubRule("R-NAME-01");
        Violation v = new Violation(1, "bad name",
                new File(Paths.get("/x/A.java"), "com.foo"), rule);
        SpyStore store = new SpyStore();
        SuppressionService service = new SuppressionService(store, null);

        int[] counts = fixCommandWith("n\n")
                .runInteractiveLoop(reportWith(v), profile(rule), service, Paths.get("/s.yaml"));

        assertEquals(0, counts[0], "newlySuppressed");
        assertEquals(1, counts[1], "remaining");
        assertTrue(store.saved.isEmpty());
    }

    @Test
    void actionQStopsLoopAndKeepsAllRemainingViolations() {
        StubRule rule = new StubRule("R-NAME-01");
        File file = new File(Paths.get("/x/A.java"), "com.foo");
        Violation v1 = new Violation(1, "v1", file, rule);
        Violation v2 = new Violation(2, "v2", file, rule);
        SpyStore store = new SpyStore();
        SuppressionService service = new SuppressionService(store, null);

        int[] counts = fixCommandWith("q\n")
                .runInteractiveLoop(reportWith(v1, v2), profile(rule), service, Paths.get("/s.yaml"));

        assertEquals(2, counts[1], "both violations remain after quit");
        assertEquals(0, counts[0], "nothing suppressed");
    }

    @Test
    void invalidInputPromptsAgainBeforeAccepting() {
        StubRule rule = new StubRule("R-NAME-01");
        Violation v = new Violation(1, "bad",
                new File(Paths.get("/x/A.java"), "com.foo"), rule);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        SpyStore store = new SpyStore();
        SuppressionService service = new SuppressionService(store, null);

        FixCommand cmd = new FixCommand(new PrintStream(buf),
                new BufferedReader(new StringReader("maybe\ny\n\n")));
        cmd.runInteractiveLoop(reportWith(v), profile(rule), service, Paths.get("/s.yaml"));

        assertTrue(buf.toString().contains("Invalid input"),
                "should re-prompt on invalid input");
    }

    private static FixCommand fixCommandWith(String input) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        return new FixCommand(new PrintStream(buf),
                new BufferedReader(new StringReader(input)));
    }

    private static ViolationReport reportWith(Violation... violations) {
        ViolationReport r = new ViolationReport();
        r.setCheckedFileCount(1);
        for (Violation v : violations) r.addViolation(v);
        return r;
    }

    private static StyleProfile profile(ComplianceRule... rules) {
        return new StyleProfile("p", List.of(rules));
    }

    private static class StubRule extends ComplianceRule {
        StubRule(String id) { super(id, "stub"); }
        @Override public List<Violation> validate(List<File> files) {
            return Collections.emptyList();
        }
    }

    private static class SpyStore implements SuppressionStore {
        final List<Suppression> saved = new ArrayList<>();
        @Override public List<Suppression> loadAll(Path f, StyleProfile p) { return saved; }
        @Override public void save(Path f, List<Suppression> s) {
            saved.clear(); saved.addAll(s);
        }
    }
}
