package com.archchecker.infrastructure.profile;

import com.archchecker.application.ProfileLoader;
import com.archchecker.domain.rule.ComplianceRule;
import com.archchecker.domain.rule.DependencyRule;
import com.archchecker.domain.rule.NamingRule;
import com.archchecker.domain.rule.PackageRule;
import com.archchecker.domain.rule.SupertypeRule;
import com.archchecker.domain.profile.StyleProfile;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlProfileLoader implements ProfileLoader {

    @Override
    @SuppressWarnings("unchecked")
    public StyleProfile load(Path profilePath) {
        if (!Files.isRegularFile(profilePath)) {
            throw new IllegalArgumentException("Profile not found: " + profilePath);
        }
        Map<String, Object> root;
        try (Reader r = Files.newBufferedReader(profilePath, StandardCharsets.UTF_8)) {
            root = new Yaml().load(r);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read profile", e);
        }
        if (root == null) {
            throw new IllegalArgumentException("Empty profile");
        }
        String name = String.valueOf(root.getOrDefault("name", "default"));
        Object rulesNode = root.get("rules");
        List<ComplianceRule> rules = new ArrayList<>();
        if (rulesNode instanceof List<?>) {
            for (Object entry : (List<?>) rulesNode) {
                if (!(entry instanceof Map<?, ?>)) continue;
                rules.add(toRule((Map<String, Object>) entry));
            }
        }
        return new StyleProfile(name, rules);
    }

    private static ComplianceRule toRule(Map<String, Object> m) {
        String type = String.valueOf(m.getOrDefault("type", ""));
        String id = String.valueOf(m.getOrDefault("id", ""));
        String desc = String.valueOf(m.getOrDefault("description", ""));
        switch (type) {
            case "naming":
                return new NamingRule(id, desc,
                        String.valueOf(m.getOrDefault("classNamePattern", ".*")));
            case "dependency":
                return new DependencyRule(id, desc,
                        String.valueOf(m.getOrDefault("sourcePackage", "**")),
                        String.valueOf(m.getOrDefault("targetPackage", "**")),
                        Boolean.TRUE.equals(m.get("isAllowed")));
            case "supertype":
                return new SupertypeRule(id, desc,
                        String.valueOf(m.getOrDefault("targetPackage", "**")),
                        String.valueOf(m.getOrDefault("requiredSupertype", "Object")));
            case "package":
                return new PackageRule(id, desc,
                        String.valueOf(m.getOrDefault("packagePattern", "**")));
            default:
                throw new IllegalArgumentException("Unknown rule type: " + type);
        }
    }
}
