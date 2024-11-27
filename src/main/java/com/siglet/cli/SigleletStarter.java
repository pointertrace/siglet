package com.siglet.cli;

import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SigleletStarter implements Runnable{

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path to config file.", required = true)
    private String filePath;


    @Override
    public void run() {
        try  {
            String configFileContent = new String(Files.readAllBytes(Paths.get(filePath)));

            Siglet siglet = new Siglet(configFileContent);

            siglet.start();

        } catch (Exception e) {
            System.out.println("error starting siglet:"+e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}
