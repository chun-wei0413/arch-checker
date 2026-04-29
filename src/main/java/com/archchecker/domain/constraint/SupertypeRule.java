package com.archchecker.domain.constraint;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupertypeRule extends ArchitectureConstraint {
    private static final Pattern CLASS_HEAD = Pattern.compile(
            "\\b(?:class|interface)\\s+(\\w+)([^\\{]*)\\{", Pattern.DOTALL);
    private final String targetPackage;
    private final String requiredSupertype;

    public SupertypeRule(String id, String description, String targetPackage,
                         String requiredSupertype) {
        super(id, description);
        this.targetPackage = targetPackage;
        this.requiredSupertype = requiredSupertype;
    }

    public String getTargetPackage() { return targetPackage; }
    public String getRequiredSupertype() { return requiredSupertype; }

    @Override
    public List<Violation> validate(List<File> files) {
        List<Violation> result = new ArrayList<>();
        for (File file : files) {
            if (!packageMatches(file.getPackageName(), targetPackage)) continue;
            String content = file.readContent();
            Matcher m = CLASS_HEAD.matcher(content);
            while (m.find()) {
                String name = m.group(1);
                String head = m.group(2);
                if (!head.contains(requiredSupertype)) {
                    int line = content.substring(0, m.start()).split("\n", -1).length;
                    String msg = "Class '" + name + "' must extend/implement '"
                            + requiredSupertype + "'";
                    result.add(new Violation(line, msg, file, this));
                }
            }
        }
        return result;
    }
}
