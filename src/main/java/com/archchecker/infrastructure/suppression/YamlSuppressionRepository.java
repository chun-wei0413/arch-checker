package com.archchecker.infrastructure.suppression;

import com.archchecker.application.SuppressionRepository;
import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.constraint.ArchitectureConstraint;
import com.archchecker.domain.profile.StyleProfile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlSuppressionRepository implements SuppressionRepository {

    @Override
    @SuppressWarnings("unchecked")
    public List<Suppression> loadAll(Path suppressionFile, StyleProfile profile) {
        List<Suppression> result = new ArrayList<>();
        if (suppressionFile == null || !Files.isRegularFile(suppressionFile)) {
            return result;
        }
        Object root;
        try (Reader r = Files.newBufferedReader(suppressionFile, StandardCharsets.UTF_8)) {
            root = new Yaml().load(r);
        } catch (IOException e) {
            return result;
        }
        if (!(root instanceof List<?>)) return result;
        for (Object entry : (List<?>) root) {
            if (!(entry instanceof Map<?, ?>)) continue;
            Suppression s = toSuppression((Map<String, Object>) entry, profile);
            if (s != null) result.add(s);
        }
        return result;
    }

    @Override
    public void save(Path suppressionFile, List<Suppression> suppressions) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (Suppression s : suppressions) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("constraintId", s.getConstraint().getId());
            m.put("filePath", s.getFilePath().toString().replace('\\', '/'));
            m.put("lineNumber", s.getLineNumber());
            m.put("reason", s.getReason());
            m.put("timestamp", s.getTimestamp().toString());
            data.add(m);
        }
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        try (Writer w = Files.newBufferedWriter(suppressionFile, StandardCharsets.UTF_8)) {
            new Yaml(opts).dump(data, w);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save suppression file", e);
        }
    }

    private static Suppression toSuppression(Map<String, Object> m, StyleProfile profile) {
        ArchitectureConstraint c = lookup(profile, String.valueOf(m.get("constraintId")));
        if (c == null) return null;
        Path file = Paths.get(String.valueOf(m.getOrDefault("filePath", "")));
        int line = ((Number) m.getOrDefault("lineNumber", 0)).intValue();
        String reason = String.valueOf(m.getOrDefault("reason", ""));
        Instant ts = Instant.parse(String.valueOf(
                m.getOrDefault("timestamp", Instant.now().toString())));
        return new Suppression(c, file, line, reason, ts);
    }

    private static ArchitectureConstraint lookup(StyleProfile profile, String id) {
        if (id == null) return null;
        for (ArchitectureConstraint c : profile.getConstraints()) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }
}
