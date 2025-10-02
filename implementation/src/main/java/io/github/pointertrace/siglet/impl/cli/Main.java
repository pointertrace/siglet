package io.github.pointertrace.siglet.impl.cli;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        new CommandLine(new SigletStarter()).execute(args);
    }
}
