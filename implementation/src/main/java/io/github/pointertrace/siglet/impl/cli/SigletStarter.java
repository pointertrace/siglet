package io.github.pointertrace.siglet.impl.cli;

import io.github.pointertrace.siglet.impl.Siglet;
import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.impl.utils.VersionRetrievers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


@CommandLine.Command(
        mixinStandardHelpOptions = true,
        description = "Siglet",
        customSynopsis = "[options]"
)
public class SigletStarter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigletStarter.class);

    private final CountDownLatch latch = new CountDownLatch(1);

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

            if (sigletPaths != null) {
                for (Path path : sigletPaths) {
                    SigletBundle sigletBundle = SigletBundle.load(path.toFile());
                    LOGGER.info("loaded siglet bundle:{}", sigletBundle.id());
                    sigletBundle.definitions().forEach(def -> {
                        LOGGER.info("  name:{}, class:{}", def.getSigletConfig().name(),def.getSigletConfig().sigletClassName());
                    });
                    sigletBundles.add(sigletBundle);
                }

            }

            Siglet siglet = new Siglet(configFileContent, sigletBundles);
            LOGGER.info("siglet initialized");

            siglet.start();

            Runtime.getRuntime().addShutdownHook(new Thread(latch::countDown));

            latch.await();
            siglet.stop();
            LOGGER.info("siglet stopped");

        } catch (Exception e) {
            LOGGER.error("Error starting Siglet", e);
            System.exit(-1);
        }
    }
}
