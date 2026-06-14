package com.archchecker.application;

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

class InteractiveFixerTest {

    @Test
    void answerYSuppressesViolationAndReturnsExitZero() {
        File file = new File(Paths.get("/x/A.java"), "com.foo");
        StubRule rule = new StubRule("R-NAME-01");
        Violation v = new Violation(1, "bad name", file, rule, "Rename it");
        ViolationReport report = reportWith(v);
        SpyStore store = new SpyStore();
        StyleProfile profile = new StyleProfile("p", List.of(rule));

        InteractiveFixer fixer = fixerWithInput("y\n", store);
        InteractiveFixer.FixResult result = fixer.fix(report, Paths.get("/s.yaml"), profile);

        assertEquals(0, result.getExitCode());
        assertEquals(1, result.getNewlySuppressed());
        assertTrue(result.getRemaining().isEmpty());
        assertEquals(1, store.saved.size());
    }

    @Test
    void answerNKeepsViolationInRemaining() {
        File file = new File(Paths.get("/x/A.java"), "com.foo");
        StubRule rule = new StubRule("R-NAME-01");
        Violation v = new Violation(1, "bad name", file, rule);
        ViolationReport report = reportWith(v);
        SpyStore store = new SpyStore();

        InteractiveFixer fixer = fixerWithInput("n\n", store);
        InteractiveFixer.FixResult result =
                fixer.fix(report, Paths.get("/s.yaml"), new StyleProfile("p", List.of(rule)));

        assertEquals(1, result.getExitCode());
        assertEquals(1, result.getRemaining().size());
        assertEquals(0, result.getNewlySuppressed());
        assertTrue(store.saved.isEmpty());
    }

    @Test
    void answerQStopsLoopAndKeepsRemainingViolations() {
        File file = new File(Paths.get("/x/A.java"), "com.foo");
        StubRule rule = new StubRule("R-NAME-01");
        Violation v1 = new Violation(1, "v1", file, rule);
        Violation v2 = new Violation(2, "v2", file, rule);
        ViolationReport report = reportWith(v1, v2);
        SpyStore store = new SpyStore();

        InteractiveFixer fixer = fixerWithInput("q\n", store);
        InteractiveFixer.FixResult result =
                fixer.fix(report, Paths.get("/s.yaml"), new StyleProfile("p", List.of(rule)));

        assertEquals(2, result.getRemaining().size(), "both violations remain after quit");
        assertEquals(0, result.getNewlySuppressed());
    }

    @Test
    void invalidInputPromptsAgainBeforeAccepting() {
        File file = new File(Paths.get("/x/A.java"), "com.foo");
        StubRule rule = new StubRule("R-NAME-01");
        Violation v = new Violation(1, "bad", file, rule);
        ViolationReport report = reportWith(v);
        SpyStore store = new SpyStore();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        InteractiveFixer fixer = new InteractiveFixer(store,
                new PrintStream(buf),
                new BufferedReader(new StringReader("maybe\ny\n")));
        fixer.fix(report, Paths.get("/s.yaml"), new StyleProfile("p", List.of(rule)));

        assertTrue(buf.toString().contains("Invalid input"),
                "should re-prompt on invalid input");
    }

    private static InteractiveFixer fixerWithInput(String input, SpyStore store) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        return new InteractiveFixer(store, new PrintStream(buf),
                new BufferedReader(new StringReader(input)));
    }

    private static ViolationReport reportWith(Violation... violations) {
        ViolationReport r = new ViolationReport();
        r.setCheckedFileCount(1);
        for (Violation v : violations) r.addViolation(v);
        return r;
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
