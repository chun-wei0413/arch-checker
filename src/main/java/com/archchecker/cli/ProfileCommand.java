package com.archchecker.cli;

import picocli.CommandLine.Command;

@Command(name = "profile",
        description = "Manage Style Profile YAML files.",
        subcommands = { ProfileInitCommand.class, ProfileValidateCommand.class })
public class ProfileCommand implements Runnable {

    @Override
    public void run() {
        System.err.println("Specify a subcommand: init, validate");
    }
}
