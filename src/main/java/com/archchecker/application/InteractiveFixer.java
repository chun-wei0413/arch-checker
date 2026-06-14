package com.archchecker.application;

import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.compliance.Violation;
import com.archchecker.domain.compliance.ViolationReport;
import com.archchecker.domain.profile.StyleProfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InteractiveFixer {
    private final SuppressionStore store;
    private final PrintStream out;
    private final BufferedReader in;

    public InteractiveFixer(SuppressionStore store, PrintStream out, BufferedReader in) {
        this.store = store;
        this.out = out;
        this.in = in;
    }

    public FixResult fix(ViolationReport report, Path suppressionFile, StyleProfile profile) {
        List<Violation> violations = new ArrayList<>(report.getViolations());
        int newlySuppressed = 0;
        List<Violation> remaining = new ArrayList<>();
        boolean quit = false;

        for (int i = 0; i < violations.size(); i++) {
            Violation v = violations.get(i);
            if (quit) {
                remaining.add(v);
                continue;
            }
            out.println();
            out.println("Violation " + (i + 1) + "/" + violations.size() + ":");
            out.println("  " + v.describe());
            if (v.hasSuggestion()) {
                out.println("  Suggestion: " + v.getSuggestion());
            }
            String answer = prompt();
            switch (answer) {
                case "y" -> {
                    suppress(v, suppressionFile, profile);
                    newlySuppressed++;
                }
                case "q" -> {
                    remaining.add(v);
                    quit = true;
                }
                default -> remaining.add(v);
            }
        }
        return new FixResult(newlySuppressed, remaining);
    }

    private String prompt() {
        while (true) {
            out.print("Suppress? [y/n/q]: ");
            try {
                String line = in.readLine();
                if (line == null) return "q";
                String trimmed = line.trim().toLowerCase();
                if (trimmed.equals("y") || trimmed.equals("yes")) return "y";
                if (trimmed.equals("n") || trimmed.equals("no")) return "n";
                if (trimmed.equals("q") || trimmed.equals("quit")) return "q";
                out.print("Invalid input. Please enter y, n, or q: ");
            } catch (IOException e) {
                return "q";
            }
        }
    }

    private void suppress(Violation v, Path suppressionFile, StyleProfile profile) {
        List<Suppression> all = new ArrayList<>(store.loadAll(suppressionFile, profile));
        all.add(new Suppression(v.getRule(), v.getFile().getFilePath(),
                v.getLineNumber(), "Suppressed interactively", Instant.now()));
        store.save(suppressionFile, all);
    }

    public static class FixResult {
        private final int newlySuppressed;
        private final List<Violation> remaining;

        public FixResult(int newlySuppressed, List<Violation> remaining) {
            this.newlySuppressed = newlySuppressed;
            this.remaining = remaining;
        }

        public int getNewlySuppressed() { return newlySuppressed; }
        public List<Violation> getRemaining() { return remaining; }
        public int getExitCode() { return remaining.isEmpty() ? 0 : 1; }
    }
}
