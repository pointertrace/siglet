package com.siglet.cli;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        new CommandLine(new SigleletStarter()).execute(args);
    }
}
