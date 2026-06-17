package com.archchecker.application;

import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.compliance.Violation;
import com.archchecker.domain.rule.ComplianceRule;
import com.archchecker.domain.profile.StyleProfile;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SuppressionService {
    private final SuppressionStore store;
    private final ProfileLoader profileLoader;

    public SuppressionService(SuppressionStore store,
                              ProfileLoader profileLoader) {
        this.store = store;
        this.profileLoader = profileLoader;
    }

    /** For SuppressCommand: resolves rule from profilePath by ruleId. */
    public Suppression suppress(Path profilePath, Path suppressionFile,
                                 String ruleId, Path filePath,
                                 int lineNumber, String reason) {
        StyleProfile profile = profileLoader.load(profilePath);
        ComplianceRule rule = findRule(profile, ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("Unknown rule id: " + ruleId);
        }
        return suppressCore(rule, filePath, lineNumber, reason, profile, suppressionFile);
    }

    /** For FixCommand: Violation and StyleProfile are already in memory. */
    public Suppression suppress(Violation violation, StyleProfile profile,
                                 Path suppressionFile, String reason) {
        return suppressCore(violation.getRule(), violation.getFile().getFilePath(), violation.getLineNumber(), reason, profile, suppressionFile);
    }

    private Suppression suppressCore(ComplianceRule rule, Path filePath,
                                      int lineNumber, String reason,
                                      StyleProfile profile, Path suppressionFile) {
        List<Suppression> all = new ArrayList<>(store.loadAll(suppressionFile, profile));
        Suppression added = new Suppression(rule, filePath, lineNumber,
                reason, Instant.now());
        all.add(added);
        store.save(suppressionFile, all);
        return added;
    }

    private ComplianceRule findRule(StyleProfile profile, String id) {
        for (ComplianceRule c : profile.getRules()) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }
}


