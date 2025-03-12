package com.siglet.cli;

import com.siglet.utils.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@CommandLine.Command(
        mixinStandardHelpOptions = true,
        description = "Siglet",
        customSynopsis = "[options]"
)
public class SigleletStarter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigleletStarter.class);

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path to config file.")
    private String filePath;

    @CommandLine.Option(names = {"-v", "--version"}, description = "Show iath to config file.")
    private boolean versionRequested;

    @Override
    public void run() {
        if (versionRequested) {
            System.out.println(String.format("Siglet version %s", Version.get()));
            System.exit(0);
        }
        if (filePath == null) {
            throw new CommandLine.ParameterException(new CommandLine(this),
                    "Missing required option: '--file=<filePath>'");
        }
        try {
            LOGGER.info("initializing siglet");

            Path configFilePath = Paths.get(filePath);
            if (!Files.exists(configFilePath)) {
                LOGGER.error("config file not found");
                System.exit(-1);
            }

            String configFileContent = new String(Files.readAllBytes(Paths.get(filePath)));
            LOGGER.info("config file path:{}", filePath);
            LOGGER.debug("config file content:{}", configFileContent);


            Siglet siglet = new Siglet(configFileContent);
            LOGGER.info("siglet initialized");

            siglet.start();
        } catch (Exception e) {
            LOGGER.error("Error starting Siglet", e);
            System.exit(-1);
        }
    }
}
