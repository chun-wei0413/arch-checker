package com.archchecker.cli;

import com.archchecker.application.SuppressionService;
import com.archchecker.domain.compliance.Suppression;
import com.archchecker.infrastructure.profile.YamlProfileRepository;
import com.archchecker.infrastructure.suppression.YamlSuppressionRepository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "suppress",
        description = "Mark a specific violation as 'known and accepted'.")
public class SuppressCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Style Profile YAML file (used to resolve constraint id).")
    private Path profilePath;

    @Parameters(index = "1", description = "Constraint id (e.g. R-NAME-01).")
    private String constraintId;

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
                new YamlSuppressionRepository(),
                new YamlProfileRepository());
        Suppression s = service.suppress(profilePath, suppressionFile,
                constraintId, filePath, lineNumber, reason);
        System.out.println("Suppressed: " + s.getConstraint().getId() + " at "
                + s.getFilePath() + ":" + s.getLineNumber());
        return 0;
    }
}
