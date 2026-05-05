package com.archchecker.domain.compliance;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.rule.ComplianceRule;

public class Violation {
    private final int lineNumber;
    private final String message;
    private final File file;
    private final ComplianceRule rule;

    public Violation(int lineNumber, String message, File file,
                     ComplianceRule rule) {
        this.lineNumber = lineNumber;
        this.message = message;
        this.file = file;
        this.rule = rule;
    }

    public int getLineNumber() { return lineNumber; }
    public String getMessage() { return message; }
    public File getFile() { return file; }
    public ComplianceRule getRule() { return rule; }

    public String describe() {
        return file.getFilePath() + ":" + lineNumber
                + " [" + rule.getId() + "] " + message;
    }
}
