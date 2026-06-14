package com.archchecker.domain.compliance;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.rule.ComplianceRule;

public class Violation {
    private final int lineNumber;
    private final String message;
    private final File file;
    private final ComplianceRule rule;
    private final String suggestion; // [0..1]

    public Violation(int lineNumber, String message, File file, ComplianceRule rule) {
        this(lineNumber, message, file, rule, null);
    }

    public Violation(int lineNumber, String message, File file,
                     ComplianceRule rule, String suggestion) {
        this.lineNumber = lineNumber;
        this.message = message;
        this.file = file;
        this.rule = rule;
        this.suggestion = suggestion;
    }

    public int getLineNumber() { return lineNumber; }
    public String getMessage() { return message; }
    public File getFile() { return file; }
    public ComplianceRule getRule() { return rule; }
    public String getSuggestion() { return suggestion; }
    public boolean hasSuggestion() { return suggestion != null && !suggestion.isBlank(); }

    public String describe() {
        return file.getFilePath() + ":" + lineNumber
                + " [" + rule.getId() + "] " + message;
    }
}
