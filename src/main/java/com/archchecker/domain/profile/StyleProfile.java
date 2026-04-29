package com.archchecker.domain.profile;

import com.archchecker.domain.constraint.ArchitectureConstraint;

import java.util.Collections;
import java.util.List;

public class StyleProfile {
    private final String name;
    private final List<ArchitectureConstraint> constraints;

    public StyleProfile(String name, List<ArchitectureConstraint> constraints) {
        this.name = name;
        this.constraints = constraints == null ? Collections.emptyList() : constraints;
    }

    public String getName() {
        return name;
    }

    public List<ArchitectureConstraint> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }
}
