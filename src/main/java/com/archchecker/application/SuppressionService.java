package com.archchecker.application;

import com.archchecker.domain.compliance.Suppression;
import com.archchecker.domain.constraint.ArchitectureConstraint;
import com.archchecker.domain.profile.StyleProfile;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SuppressionService {
    private final SuppressionRepository repository;
    private final ProfileRepository profileRepository;

    public SuppressionService(SuppressionRepository repository,
                              ProfileRepository profileRepository) {
        this.repository = repository;
        this.profileRepository = profileRepository;
    }

    public Suppression suppress(Path profilePath, Path suppressionFile,
                                 String constraintId, Path filePath,
                                 int lineNumber, String reason) {
        StyleProfile profile = profileRepository.load(profilePath);
        ArchitectureConstraint constraint = findConstraint(profile, constraintId);
        if (constraint == null) {
            throw new IllegalArgumentException("Unknown constraint id: " + constraintId);
        }
        List<Suppression> all = new ArrayList<>(
                repository.loadAll(suppressionFile, profile));
        Suppression added = new Suppression(constraint, filePath, lineNumber,
                reason, Instant.now());
        all.add(added);
        repository.save(suppressionFile, all);
        return added;
    }

    private ArchitectureConstraint findConstraint(StyleProfile profile, String id) {
        for (ArchitectureConstraint c : profile.getConstraints()) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }
}
