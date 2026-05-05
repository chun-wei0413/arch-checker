package com.archchecker.domain.profile;

import com.archchecker.domain.rule.ComplianceRule;

import java.util.Collections;
import java.util.List;

public class StyleProfile {
    private final String name;
    private final List<ComplianceRule> rules;

    public StyleProfile(String name, List<ComplianceRule> rules) {
        this.name = name;
        this.rules = rules == null ? Collections.emptyList() : rules;
    }

    public String getName() {
        return name;
    }

    public List<ComplianceRule> getRules() {
        return Collections.unmodifiableList(rules);
    }
}
