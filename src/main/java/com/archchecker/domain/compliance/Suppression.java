package com.archchecker.domain.compliance;

import com.archchecker.domain.constraint.ArchitectureConstraint;

import java.nio.file.Path;
import java.time.Instant;

public class Suppression {
    private final ArchitectureConstraint constraint;
    private final Path filePath;
    private final int lineNumber;
    private final String reason;
    private final Instant timestamp;

    public Suppression(ArchitectureConstraint constraint, Path filePath,
                       int lineNumber, String reason, Instant timestamp) {
        this.constraint = constraint;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public ArchitectureConstraint getConstraint() { return constraint; }
    public Path getFilePath() { return filePath; }
    public int getLineNumber() { return lineNumber; }
    public String getReason() { return reason; }
    public Instant getTimestamp() { return timestamp; }

    public boolean matches(Violation v) {
        return v.getConstraint().getId().equals(constraint.getId())
                && v.getFile().getFilePath().equals(filePath)
                && v.getLineNumber() == lineNumber;
    }
}
