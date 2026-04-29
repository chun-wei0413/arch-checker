package com.archchecker.application;

import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.profile.StyleProfile;

import java.nio.file.Path;
import java.util.List;

public interface SuppressionRepository {
    List<Suppression> loadAll(Path suppressionFile, StyleProfile profile);

    void save(Path suppressionFile, List<Suppression> suppressions);
}
