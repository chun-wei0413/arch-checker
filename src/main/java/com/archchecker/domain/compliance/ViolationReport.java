package com.archchecker.domain.compliance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViolationReport {
    private int checkedFileCount;
    private int suppressedCount;
    private final List<Violation> violations = new ArrayList<>();

    public void setCheckedFileCount(int n) {
        this.checkedFileCount = n;
    }

    public void incrementSuppressed() {
        this.suppressedCount++;
    }

    public void addViolation(Violation v) {
        violations.add(v);
    }

    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public int getCheckedFileCount() { return checkedFileCount; }
    public int getViolationCount() { return violations.size(); }
    public int getSuppressedCount() { return suppressedCount; }

    public String summary() {
        return "Checked " + checkedFileCount + " file(s); "
                + violations.size() + " violation(s); "
                + suppressedCount + " suppressed.";
    }
}
