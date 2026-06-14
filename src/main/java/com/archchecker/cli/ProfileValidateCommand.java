package com.archchecker.cli;

import com.archchecker.application.ProfileValidateService;
import com.archchecker.infrastructure.profile.YamlProfileLoader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "validate",
        description = "Validate a Style Profile YAML and display loaded rules.")
public class ProfileValidateCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Style Profile YAML file to validate.")
    private Path profilePath;

    @Override
    public Integer call() {
        ProfileValidateService service =
                new ProfileValidateService(new YamlProfileLoader());
        try {
            ProfileValidateService.ValidateResult result = service.validate(profilePath);
            System.out.println("Profile '" + result.getProfileName()
                    + "' is valid. Loaded " + result.getRuleCount() + " rule(s):");
            for (String desc : result.getRuleDescriptions()) {
                System.out.println("  - " + desc);
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
            return 2;
        }
    }
}
