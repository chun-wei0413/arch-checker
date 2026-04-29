package com.archchecker.domain.constraint;

import com.archchecker.domain.codebase.File;
import com.archchecker.domain.compliance.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamingRule extends ArchitectureConstraint {
    private static final Pattern CLASS_DECL = Pattern.compile(
            "\\b(?:class|interface|enum)\\s+(\\w+)");
    private final String classNamePattern;

    public NamingRule(String id, String description, String classNamePattern) {
        super(id, description);
        this.classNamePattern = classNamePattern;
    }

    public String getClassNamePattern() {
        return classNamePattern;
    }

    @Override
    public List<Violation> validate(List<File> files) {
        List<Violation> result = new ArrayList<>();
        for (File file : files) {
            String content = file.readContent();
            Matcher m = CLASS_DECL.matcher(content);
            while (m.find()) {
                String name = m.group(1);
                if (!name.matches(classNamePattern)) {
                    int line = lineOf(content, m.start());
                    String msg = "Class '" + name + "' violates naming pattern '"
                            + classNamePattern + "'";
                    result.add(new Violation(line, msg, file, this));
                }
            }
        }
        return result;
    }

    private static int lineOf(String content, int offset) {
        int line = 1;
        for (int i = 0; i < offset && i < content.length(); i++) {
            if (content.charAt(i) == '\n') line++;
        }
        return line;
    }
}
