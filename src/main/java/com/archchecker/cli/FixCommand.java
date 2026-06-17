package com.archchecker.cli;

import com.archchecker.application.ComplianceCheckService;
import com.archchecker.application.SuppressionService;
import com.archchecker.domain.compliance.Violation;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

    private final PrintStream out;
    private final BufferedReader in;

    public FixCommand() {
        this(System.out, new BufferedReader(new InputStreamReader(System.in)));
    }

    FixCommand(PrintStream out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public Integer call() {
        YamlProfileLoader profileLoader = new YamlProfileLoader();
        YamlSuppressionStore store = new YamlSuppressionStore();
        SuppressionService suppressionService = new SuppressionService(store, profileLoader);

        ComplianceCheckService checkService = new ComplianceCheckService(
                new JavaParserAdapter(), profileLoader, store);
        ComplianceCheckService.CheckResult checkResult =
                checkService.run(projectPath, profilePath, suppressionFile);

        ViolationReport report = checkResult.getReport();
        if (report.getViolationCount() == 0) {
            new ConsoleReporter().render(report);
            return checkResult.getExitCode();
        }

        StyleProfile profile = profileLoader.load(profilePath);
        int[] counts = runInteractiveLoop(report, profile, suppressionService, suppressionFile);
        int newlySuppressed = counts[0];
        int remaining = counts[1];

        out.println();
        out.println("--");
        out.println("Checked " + report.getCheckedFileCount() + " file(s); "
                + remaining + " violation(s) remaining; "
                + (report.getSuppressedCount() + newlySuppressed) + " suppressed.");
        return remaining == 0 ? 0 : 1;
    }

    int[] runInteractiveLoop(ViolationReport report, StyleProfile profile,
                             SuppressionService service, Path suppressFile) {
        List<Violation> violations = new ArrayList<>(report.getViolations());
        int newlySuppressed = 0;
        int remaining = 0;
        boolean quit = false;

        for (int i = 0; i < violations.size(); i++) {
            Violation v = violations.get(i);
            if (quit) { remaining++; continue; }

            out.println();
            out.println("Violation " + (i + 1) + "/" + violations.size() + ":");
            out.println("  " + v.describe());
            if (v.hasSuggestion()) out.println("  Suggestion: " + v.getSuggestion());

            switch (readAction()) {
                case "y" -> {
                    service.suppress(v, profile, suppressFile, "Suppressed interactively");
                    newlySuppressed++;
                }
                case "q" -> { remaining++; quit = true; }
                default  -> remaining++;
            }
        }
        return new int[]{newlySuppressed, remaining};
    }

    private String readAction() {
        while (true) {
            out.print("Suppress? [y/n/q]: ");
            try {
                String line = in.readLine();
                if (line == null) return "q";
                String t = line.trim().toLowerCase();
                if (t.equals("y") || t.equals("yes")) return "y";
                if (t.equals("n") || t.equals("no"))  return "n";
                if (t.equals("q") || t.equals("quit")) return "q";
                out.print("Invalid input. Please enter y, n, or q: ");
            } catch (IOException e) {
                return "q";
            }
        }
    }

}
