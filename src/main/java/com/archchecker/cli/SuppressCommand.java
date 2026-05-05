package com.archchecker.cli;

import com.archchecker.application.SuppressionService;
import com.archchecker.domain.compliance.Suppression;
import com.archchecker.infrastructure.profile.YamlProfileLoader;
import com.archchecker.infrastructure.suppression.YamlSuppressionStore;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "suppress",
        description = "Mark a specific violation as 'known and accepted'.")
public class SuppressCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Style Profile YAML file (used to resolve rule id).")
    private Path profilePath;

    @Parameters(index = "1", description = "Rule id (e.g. R-NAME-01).")
    private String ruleId;

    @Parameters(index = "2", description = "Path of the file to suppress in.")
    private Path filePath;

    @Parameters(index = "3", description = "Line number to suppress at.")
    private int lineNumber;

    @Parameters(index = "4", description = "Reason for the suppression.")
    private String reason;

    @Option(names = {"-s", "--suppress-file"},
            description = "Suppression file (default: .arch-checker-suppress.yaml).",
            defaultValue = ".arch-checker-suppress.yaml")
    private Path suppressionFile;

    @Override
    public Integer call() {
        SuppressionService service = new SuppressionService(
                new YamlSuppressionStore(),
                new YamlProfileLoader());
        Suppression s = service.suppress(profilePath, suppressionFile,
                ruleId, filePath, lineNumber, reason);
        System.out.println("Suppressed: " + s.getRule().getId() + " at "
                + s.getFilePath() + ":" + s.getLineNumber());
        return 0;
    }
}
