package com.archchecker.domain.constraint;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Violation;

import java.util.List;

public abstract class ArchitectureConstraint {
    protected final String id;
    protected final String description;

    protected ArchitectureConstraint(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public abstract List<Violation> validate(List<File> files);

    protected static boolean packageMatches(String packageName, String pattern) {
        String regex = pattern
                .replace(".", "\\.")
                .replace("**", "<<DS>>")
                .replace("*", "[^.]*")
                .replace("<<DS>>", ".*");
        return packageName.matches(regex);
    }
}
