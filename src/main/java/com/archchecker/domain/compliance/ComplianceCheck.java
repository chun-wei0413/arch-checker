package com.archchecker.domain.compliance;

import com.archchecker.domain.codebase.Project;
import com.archchecker.domain.constraint.ArchitectureConstraint;
import com.archchecker.domain.profile.StyleProfile;

import java.time.LocalDateTime;
import java.util.List;

public class ComplianceCheck {
    public static final int EXIT_PASS = 0;
    public static final int EXIT_VIOLATIONS = 1;

    private final LocalDateTime dateTime;
    private final Project project;
    private final StyleProfile profile;
    private final List<Suppression> suppressions;
    private int exitCode;

    public ComplianceCheck(Project project, StyleProfile profile,
                           List<Suppression> suppressions) {
        this.dateTime = LocalDateTime.now();
        this.project = project;
        this.profile = profile;
        this.suppressions = suppressions;
    }

    public ViolationReport run() {
        ViolationReport report = new ViolationReport();
        report.setCheckedFileCount(project.listFiles().size());
        for (ArchitectureConstraint c : profile.getConstraints()) {
            for (Violation v : c.validate(project.listFiles())) {
                if (isSuppressed(v)) {
                    report.incrementSuppressed();
                } else {
                    report.addViolation(v);
                }
            }
        }
        exitCode = report.getViolationCount() == 0 ? EXIT_PASS : EXIT_VIOLATIONS;
        return report;
    }

    private boolean isSuppressed(Violation v) {
        for (Suppression s : suppressions) {
            if (s.matches(v)) return true;
        }
        return false;
    }

    public int getExitCode() { return exitCode; }
    public LocalDateTime getDateTime() { return dateTime; }
}
