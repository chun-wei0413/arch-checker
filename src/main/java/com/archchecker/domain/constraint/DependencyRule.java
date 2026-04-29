package com.archchecker.domain.constraint;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyRule extends ArchitectureConstraint {
    private static final Pattern IMPORT = Pattern.compile(
            "^\\s*import\\s+(?:static\\s+)?([\\w.]+);", Pattern.MULTILINE);
    private final String sourcePackage;
    private final String targetPackage;
    private final boolean isAllowed;

    public DependencyRule(String id, String description, String sourcePackage,
                          String targetPackage, boolean isAllowed) {
        super(id, description);
        this.sourcePackage = sourcePackage;
        this.targetPackage = targetPackage;
        this.isAllowed = isAllowed;
    }

    public String getSourcePackage() { return sourcePackage; }
    public String getTargetPackage() { return targetPackage; }
    public boolean isAllowed() { return isAllowed; }

    @Override
    public List<Violation> validate(List<File> files) {
        List<Violation> result = new ArrayList<>();
        for (File file : files) {
            if (!packageMatches(file.getPackageName(), sourcePackage)) continue;
            String content = file.readContent();
            Matcher m = IMPORT.matcher(content);
            while (m.find()) {
                String imported = m.group(1);
                String importedPackage = imported.contains(".")
                        ? imported.substring(0, imported.lastIndexOf('.'))
                        : "";
                boolean hits = packageMatches(importedPackage, targetPackage);
                if (hits != isAllowed) {
                    int line = content.substring(0, m.start()).split("\n", -1).length;
                    String msg = "Package '" + file.getPackageName()
                            + "' " + (isAllowed ? "must depend on" : "must not depend on")
                            + " '" + targetPackage + "' (import: " + imported + ")";
                    result.add(new Violation(line, msg, file, this));
                }
            }
        }
        return result;
    }
}
