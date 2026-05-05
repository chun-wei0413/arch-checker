package com.archchecker.application;

import com.archchecker.domain.codebase.Project;
import com.archchecker.domain.compliance.ComplianceCheck;
import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.compliance.ViolationReport;
import com.archchecker.domain.profile.StyleProfile;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ComplianceCheckService {
    private final CodeParser codeParser;
    private final ProfileLoader profileLoader;
    private final SuppressionStore suppressionStore;

    public ComplianceCheckService(CodeParser codeParser,
                                  ProfileLoader profileLoader,
                                  SuppressionStore suppressionStore) {
        this.codeParser = codeParser;
        this.profileLoader = profileLoader;
        this.suppressionStore = suppressionStore;
    }

    public CheckResult run(Path projectPath, Path profilePath, Path suppressionFile) {
        StyleProfile profile = profileLoader.load(profilePath);
        Project project = codeParser.parseProject(projectPath);
        List<Suppression> suppressions = suppressionFile == null
                ? Collections.emptyList()
                : suppressionStore.loadAll(suppressionFile, profile);
        ComplianceCheck check = new ComplianceCheck(project, profile, suppressions);
        ViolationReport report = check.run();
        return new CheckResult(report, check.getExitCode());
    }

    public static class CheckResult {
        private final ViolationReport report;
        private final int exitCode;

        public CheckResult(ViolationReport report, int exitCode) {
            this.report = report;
            this.exitCode = exitCode;
        }

        public ViolationReport getReport() { return report; }
        public int getExitCode() { return exitCode; }
    }
}
