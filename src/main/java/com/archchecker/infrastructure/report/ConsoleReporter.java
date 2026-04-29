package com.archchecker.infrastructure.report;

import com.archchecker.application.Reporter;
import com.archchecker.domain.compliance.Violation;
import com.archchecker.domain.compliance.ViolationReport;

import java.io.PrintStream;

public class ConsoleReporter implements Reporter {
    private final PrintStream out;

    public ConsoleReporter(PrintStream out) {
        this.out = out;
    }

    public ConsoleReporter() {
        this(System.out);
    }

    @Override
    public void render(ViolationReport report) {
        for (Violation v : report.getViolations()) {
            out.println(v.describe());
        }
        out.println("--");
        out.println(report.summary());
    }
}
