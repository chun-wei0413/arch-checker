package com.archchecker.domain.compliance;

import com.archchecker.domain.rule.ComplianceRule;

import java.nio.file.Path;
import java.time.Instant;

public class Suppression {
    private final ComplianceRule rule;
    private final Path filePath;
    private final int lineNumber;
    private final String reason;
    private final Instant timestamp;

    public Suppression(ComplianceRule rule, Path filePath,
                       int lineNumber, String reason, Instant timestamp) {
        this.rule = rule;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public ComplianceRule getRule() { return rule; }
    public Path getFilePath() { return filePath; }
    public int getLineNumber() { return lineNumber; }
    public String getReason() { return reason; }
    public Instant getTimestamp() { return timestamp; }

    public boolean matches(Violation v) {
        return v.getRule().getId().equals(rule.getId())
                && v.getFile().getFilePath().equals(filePath)
                && v.getLineNumber() == lineNumber;
    }
}
