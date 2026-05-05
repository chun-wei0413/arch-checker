package com.archchecker.application;

import com.archchecker.domain.compliance.Suppression;
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

    public Suppression suppress(Path profilePath, Path suppressionFile,
                                 String ruleId, Path filePath,
                                 int lineNumber, String reason) {
        StyleProfile profile = profileLoader.load(profilePath);
        ComplianceRule rule = findRule(profile, ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("Unknown rule id: " + ruleId);
        }
        List<Suppression> all = new ArrayList<>(
                store.loadAll(suppressionFile, profile));
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
