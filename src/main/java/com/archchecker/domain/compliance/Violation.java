package com.archchecker.domain.compliance;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.constraint.ArchitectureConstraint;

public class Violation {
    private final int lineNumber;
    private final String message;
    private final File file;
    private final ArchitectureConstraint constraint;

    public Violation(int lineNumber, String message, File file,
                     ArchitectureConstraint constraint) {
        this.lineNumber = lineNumber;
        this.message = message;
        this.file = file;
        this.constraint = constraint;
    }

    public int getLineNumber() { return lineNumber; }
    public String getMessage() { return message; }
    public File getFile() { return file; }
    public ArchitectureConstraint getConstraint() { return constraint; }

    public String describe() {
        return file.getFilePath() + ":" + lineNumber
                + " [" + constraint.getId() + "] " + message;
    }
}
