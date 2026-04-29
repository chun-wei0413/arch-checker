package com.archchecker.cli;

import com.archchecker.application.ComplianceCheckService;
import com.archchecker.application.Reporter;
import com.archchecker.infrastructure.parser.JavaParserAdapter;
import com.archchecker.infrastructure.profile.YamlProfileRepository;
import com.archchecker.infrastructure.report.ConsoleReporter;
import com.archchecker.infrastructure.report.JsonReporter;
import com.archchecker.infrastructure.suppression.YamlSuppressionRepository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "check",
        description = "Check architecture compliance of a Java project against a Style Profile.")
public class CheckCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Java project root directory.")
    private Path projectPath;

    @Parameters(index = "1", description = "Style Profile YAML file.")
    private Path profilePath;

    @Option(names = {"-s", "--suppress-file"},
            description = "Suppression file (default: .arch-checker-suppress.yaml).",
            defaultValue = ".arch-checker-suppress.yaml")
    private Path suppressionFile;

    @Option(names = "--json", description = "Emit JSON report instead of console text.")
    private boolean jsonOutput;

    @Override
    public Integer call() {
        ComplianceCheckService service = new ComplianceCheckService(
                new JavaParserAdapter(),
                new YamlProfileRepository(),
                new YamlSuppressionRepository());
        Reporter reporter = jsonOutput ? new JsonReporter() : new ConsoleReporter();
        ComplianceCheckService.CheckResult r =
                service.run(projectPath, profilePath, suppressionFile);
        reporter.render(r.getReport());
        return r.getExitCode();
    }
}
