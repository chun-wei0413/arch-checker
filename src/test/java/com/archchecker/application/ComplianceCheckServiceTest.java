package com.archchecker.application;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.codebase.Project;
import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.compliance.Violation;
import com.archchecker.domain.rule.ComplianceRule;
import com.archchecker.domain.profile.StyleProfile;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComplianceCheckServiceTest {

    @Test
    void runReturnsExitZeroWhenNoViolations() {
        FakeParser parser = new FakeParser(new Project(Paths.get("/x"),
                List.of(new File(Paths.get("/x/A.java"), "com.foo"))));
        FakeProfileLoader profileRepo = new FakeProfileLoader(
                new StyleProfile("p", List.of(new AlwaysPassRule())));
        FakeSupStore supRepo = new FakeSupStore(Collections.emptyList());
        ComplianceCheckService service = new ComplianceCheckService(parser, profileRepo, supRepo);

        ComplianceCheckService.CheckResult r = service.run(
                Paths.get("/x"), Paths.get("/p.yaml"), Paths.get("/s.yaml"));

        assertEquals(0, r.getExitCode());
        assertEquals(0, r.getReport().getViolationCount());
        assertEquals(1, r.getReport().getCheckedFileCount());
    }

    @Test
    void runReturnsExitOneWhenViolationsFound() {
        File file = new File(Paths.get("/x/A.java"), "com.foo");
        FakeParser parser = new FakeParser(new Project(Paths.get("/x"), List.of(file)));
        AlwaysFailRule rule = new AlwaysFailRule(file);
        FakeProfileLoader profileRepo = new FakeProfileLoader(
                new StyleProfile("p", List.of(rule)));
        FakeSupStore supRepo = new FakeSupStore(Collections.emptyList());
        ComplianceCheckService service = new ComplianceCheckService(parser, profileRepo, supRepo);

        ComplianceCheckService.CheckResult r = service.run(
                Paths.get("/x"), Paths.get("/p.yaml"), Paths.get("/s.yaml"));

        assertEquals(1, r.getExitCode());
        assertEquals(1, r.getReport().getViolationCount());
    }

    @Test
    void suppressedViolationIsExcludedFromReport() {
        File file = new File(Paths.get("/x/A.java"), "com.foo");
        FakeParser parser = new FakeParser(new Project(Paths.get("/x"), List.of(file)));
        AlwaysFailRule rule = new AlwaysFailRule(file);
        Suppression sup = new Suppression(rule, file.getFilePath(), 1, "ack", Instant.now());
        FakeProfileLoader profileRepo = new FakeProfileLoader(
                new StyleProfile("p", List.of(rule)));
        FakeSupStore supRepo = new FakeSupStore(List.of(sup));
        ComplianceCheckService service = new ComplianceCheckService(parser, profileRepo, supRepo);

        ComplianceCheckService.CheckResult r = service.run(
                Paths.get("/x"), Paths.get("/p.yaml"), Paths.get("/s.yaml"));

        assertEquals(0, r.getExitCode());
        assertEquals(0, r.getReport().getViolationCount());
        assertEquals(1, r.getReport().getSuppressedCount());
    }

    private static class FakeParser implements CodeParser {
        private final Project project;
        FakeParser(Project p) { this.project = p; }
        @Override public Project parseProject(Path rootPath) { return project; }
    }

    private static class FakeProfileLoader implements ProfileLoader {
        private final StyleProfile profile;
        FakeProfileLoader(StyleProfile p) { this.profile = p; }
        @Override public StyleProfile load(Path profilePath) { return profile; }
    }

    private static class FakeSupStore implements SuppressionStore {
        private final List<Suppression> stored;
        FakeSupStore(List<Suppression> s) { this.stored = new ArrayList<>(s); }
        @Override public List<Suppression> loadAll(Path f, StyleProfile p) { return stored; }
        @Override public void save(Path f, List<Suppression> s) { stored.clear(); stored.addAll(s); }
    }

    private static class AlwaysPassRule extends ComplianceRule {
        AlwaysPassRule() { super("PASS", "always pass"); }
        @Override public List<Violation> validate(List<File> files) {
            return Collections.emptyList();
        }
    }

    private static class AlwaysFailRule extends ComplianceRule {
        private final File reportedFile;
        AlwaysFailRule(File f) { super("FAIL", "always fail"); this.reportedFile = f; }
        @Override public List<Violation> validate(List<File> files) {
            return List.of(new Violation(1, "synthetic violation", reportedFile, this));
        }
    }
}
