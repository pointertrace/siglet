package io.github.pointertrace.siglet.container.cli;

import io.github.pointertrace.siglet.container.Siglet;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.utils.VersionRetrievers;
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


            List<SigletBundle> sigletBundles = new ArrayList<>();

            for(Path path : sigletPaths) {
                sigletBundles.add(SigletBundle.load(path.toFile()));
            }

            Siglet siglet = new Siglet(configFileContent, sigletBundles);
            LOGGER.info("siglet initialized");

            siglet.start();
        } catch (Exception e) {
            LOGGER.error("Error starting Siglet", e);
            System.exit(-1);
        }
    }
}
