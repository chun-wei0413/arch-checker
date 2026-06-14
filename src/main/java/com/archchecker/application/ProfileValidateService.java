package com.archchecker.application;

import com.archchecker.domain.profile.StyleProfile;
import com.archchecker.domain.rule.ComplianceRule;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProfileValidateService {
    private final ProfileLoader profileLoader;

    public ProfileValidateService(ProfileLoader profileLoader) {
        this.profileLoader = profileLoader;
    }

    public ValidateResult validate(Path profilePath) {
        StyleProfile profile = profileLoader.load(profilePath);
        List<String> ruleIds = new ArrayList<>();
        for (ComplianceRule r : profile.getRules()) {
            ruleIds.add(r.getId() + " (" + r.getClass().getSimpleName() + ")");
        }
        return new ValidateResult(profile.getName(), ruleIds);
    }

    public static class ValidateResult {
        private final String profileName;
        private final List<String> ruleDescriptions;

        public ValidateResult(String profileName, List<String> ruleDescriptions) {
            this.profileName = profileName;
            this.ruleDescriptions = ruleDescriptions;
        }

        public String getProfileName() { return profileName; }
        public int getRuleCount() { return ruleDescriptions.size(); }
        public List<String> getRuleDescriptions() { return ruleDescriptions; }
    }
}
