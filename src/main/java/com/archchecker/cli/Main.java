package com.archchecker.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "arch-checker",
        version = "arch-checker 0.1.0",
        mixinStandardHelpOptions = true,
        subcommands = { CheckCommand.class, SuppressCommand.class,
                        ProfileCommand.class, FixCommand.class },
        description = "A Fluent Architectural Style Compliance Tool for Java.")
public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main())
                .setExecutionExceptionHandler((ex, cmd, parseResult) -> {
                    cmd.getErr().println("Error: " + ex.getMessage());
                    return 2;
                })
                .execute(args);
        System.exit(exitCode);
    }
}
