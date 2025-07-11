package com.siglet.container.cli;

import com.siglet.container.Siglet;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.config.siglet.SigletConfigLoader;
import com.siglet.utils.VersionRetrievers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@CommandLine.Command(
        mixinStandardHelpOptions = true,
        description = "Siglet",
        customSynopsis = "[options]"
)
public class SigletStarter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigletStarter.class);

    @CommandLine.Option(
            names = {"-c", "--config"},
            required = true,
            defaultValue = "${env:SGL_FILE}",
            description = "Path to config file.")
    private Path configPath;

    @CommandLine.Option(
            names = {"-s", "--siglet"},
            defaultValue = "${env:SGL_SIGLET}",
            description = "Path to siglet file.")
    private List<Path> sigletPaths;

    @CommandLine.Option(
            names = {"-v", "--version"},
            description = "Show options.")
    private boolean versionRequested;

    @Override
    public void run() {
        if (versionRequested) {
            System.out.println(String.format("Siglet version %s", VersionRetrievers.get()));
            System.exit(0);
        }
        try {
            LOGGER.info("initializing siglet");

            if (!Files.exists(configPath)) {
                LOGGER.error("config file not found");
                System.exit(-1);
            }

            String configFileContent = new String(Files.readAllBytes(configPath));
            LOGGER.info("config file path:{}", configPath);
            LOGGER.debug("config file content:{}", configFileContent);


            SigletConfigLoader sigletConfigLoader = new SigletConfigLoader();
            List<SigletConfig> sigletsConfigs = new ArrayList<>();
            for(Path path : sigletPaths) {
                sigletsConfigs.add(sigletConfigLoader.load(path));
            }

            Siglet siglet = new Siglet(configFileContent, sigletsConfigs);
            LOGGER.info("siglet initialized");

            siglet.start();
        } catch (Exception e) {
            LOGGER.error("Error starting Siglet", e);
            System.exit(-1);
        }
    }
}
