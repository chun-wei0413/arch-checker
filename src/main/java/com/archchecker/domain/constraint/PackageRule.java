package com.archchecker.domain.constraint;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Violation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageRule extends ArchitectureConstraint {
    private final String packagePattern;

    public PackageRule(String id, String description, String packagePattern) {
        super(id, description);
        this.packagePattern = packagePattern;
    }

    public String getPackagePattern() {
        return packagePattern;
    }

    @Override
    public List<Violation> validate(List<File> files) {
        List<Violation> result = new ArrayList<>();
        Set<String> matched = new HashSet<>();
        for (File file : files) {
            String pkg = file.getPackageName();
            if (packageMatches(pkg, packagePattern)) {
                matched.add(pkg);
            }
        }
        if (matched.isEmpty() && !files.isEmpty()) {
            String msg = "Required package pattern '" + packagePattern
                    + "' not present in the project";
            result.add(new Violation(0, msg, files.get(0), this));
        }
        return result;
    }
}
