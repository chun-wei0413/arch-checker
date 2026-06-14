package com.archchecker.cli;

import com.archchecker.application.ComplianceCheckService;
import com.archchecker.application.InteractiveFixer;
import com.archchecker.domain.compliance.ViolationReport;
import com.archchecker.domain.profile.StyleProfile;
import com.archchecker.infrastructure.parser.JavaParserAdapter;
import com.archchecker.infrastructure.profile.YamlProfileLoader;
import com.archchecker.infrastructure.report.ConsoleReporter;
import com.archchecker.infrastructure.suppression.YamlSuppressionStore;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "fix",
        description = "Check architecture compliance with interactive fix suggestions.")
public class FixCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Java project root directory.")
    private Path projectPath;

    @Parameters(index = "1", description = "Style Profile YAML file.")
    private Path profilePath;

    @Option(names = {"-s", "--suppress-file"},
            description = "Suppression file (default: .arch-checker-suppress.yaml).",
            defaultValue = ".arch-checker-suppress.yaml")
    private Path suppressionFile;

    @Override
    public Integer call() {
        YamlProfileLoader profileLoader = new YamlProfileLoader();
        YamlSuppressionStore store = new YamlSuppressionStore();

        ComplianceCheckService checkService = new ComplianceCheckService(
                new JavaParserAdapter(), profileLoader, store);
        ComplianceCheckService.CheckResult result =
                checkService.run(projectPath, profilePath, suppressionFile);

        ViolationReport report = result.getReport();
        if (report.getViolationCount() == 0) {
            new ConsoleReporter().render(report);
            return result.getExitCode();
        }

        StyleProfile profile = profileLoader.load(profilePath);
        InteractiveFixer fixer = new InteractiveFixer(
                store, System.out,
                new BufferedReader(new InputStreamReader(System.in)));
        InteractiveFixer.FixResult fixResult =
                fixer.fix(report, suppressionFile, profile);

        System.out.println();
        System.out.println("--");
        System.out.println("Checked " + report.getCheckedFileCount() + " file(s); "
                + fixResult.getRemaining().size() + " violation(s) remaining; "
                + (report.getSuppressedCount() + fixResult.getNewlySuppressed())
                + " suppressed.");
        return fixResult.getExitCode();
    }
}
