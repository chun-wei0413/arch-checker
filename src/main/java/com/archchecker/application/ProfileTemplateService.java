package com.archchecker.application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProfileTemplateService {

    private static final String TEMPLATE =
        "# arch-checker Style Profile\n"
        + "# See https://github.com/your-org/arch-checker for full documentation.\n"
        + "name: my-profile\n"
        + "rules:\n"
        + "  # NamingRule: enforce a class name regex pattern\n"
        + "  - id: R-NAME-01\n"
        + "    type: naming\n"
        + "    description: \"Service classes must end with 'Service'\"\n"
        + "    classNamePattern: \".*Service\"\n"
        + "\n"
        + "  # DependencyRule: control allowed/forbidden package dependencies\n"
        + "  - id: R-DEP-01\n"
        + "    type: dependency\n"
        + "    description: \"Domain must not depend on infrastructure\"\n"
        + "    sourcePackage: \"**.domain.**\"\n"
        + "    targetPackage: \"**.infrastructure.**\"\n"
        + "    isAllowed: false\n"
        + "\n"
        + "  # SupertypeRule: require classes in a package to extend/implement a type\n"
        + "  - id: R-SUP-01\n"
        + "    type: supertype\n"
        + "    description: \"All rule classes must extend ComplianceRule\"\n"
        + "    targetPackage: \"**.domain.rule\"\n"
        + "    requiredSupertype: \"ComplianceRule\"\n"
        + "\n"
        + "  # PackageRule: assert a package pattern exists in the project\n"
        + "  - id: R-PKG-01\n"
        + "    type: package\n"
        + "    description: \"Domain layer must exist\"\n"
        + "    packagePattern: \"**.domain.**\"\n";

    public void generateTemplate(Path outputPath) {
        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            Files.writeString(outputPath, TEMPLATE, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write profile template: " + e.getMessage(), e);
        }
    }
}
