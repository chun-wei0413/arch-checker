package com.archchecker.cli;

import com.archchecker.application.ProfileTemplateService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "init",
        description = "Generate a Style Profile YAML template at the specified path.")
public class ProfileInitCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Output path for the generated YAML template.")
    private Path outputPath;

    @Override
    public Integer call() {
        new ProfileTemplateService().generateTemplate(outputPath);
        System.out.println("Profile template written to: " + outputPath);
        return 0;
    }
}
