package com.archchecker.infrastructure.report;

import com.archchecker.application.Reporter;
import com.archchecker.domain.compliance.Violation;
import com.archchecker.domain.compliance.ViolationReport;

import java.io.PrintStream;

public class JsonReporter implements Reporter {
    private final PrintStream out;

    public JsonReporter(PrintStream out) {
        this.out = out;
    }

    public JsonReporter() {
        this(System.out);
    }

    @Override
    public void render(ViolationReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"checkedFiles\":").append(report.getCheckedFileCount());
        sb.append(",\"violationCount\":").append(report.getViolationCount());
        sb.append(",\"suppressedCount\":").append(report.getSuppressedCount());
        sb.append(",\"violations\":[");
        boolean first = true;
        for (Violation v : report.getViolations()) {
            if (!first) sb.append(",");
            sb.append("{\"file\":\"").append(escape(v.getFile().getFilePath().toString()));
            sb.append("\",\"line\":").append(v.getLineNumber());
            sb.append(",\"ruleId\":\"").append(escape(v.getConstraint().getId()));
            sb.append("\",\"message\":\"").append(escape(v.getMessage())).append("\"}");
            first = false;
        }
        sb.append("]}");
        out.println(sb);
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
