package io.github.pointertrace.siglet.container.config.siglet;

import java.io.File;

public class ExampleJarsInfo {


    public static File getFatJarExampleSigletFile() {
        if (System.getProperty("project.build.directory") == null) {
            if (System.getProperty("user.dir") == null) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            String basePath = System.getProperty("user.dir") + "/../test-bundle/fatjar/target";
            File baseDirectory = new File(basePath);
            if (!baseDirectory.exists()) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            File[] jarFiles = baseDirectory.listFiles((dir, name) -> name.startsWith("fatjar-suffix-spanlet-") &&
                                                                  name.endsWith("SNAPSHOT.jar"));
            if (jarFiles.length != 1) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            return jarFiles[0];
        } else {
            throw new IllegalStateException("Could not determine project directories do get bundle jars");
        }

    }

    public static File getSpringBootExampleSigletFile() {
        if (System.getProperty("project.build.directory") == null) {
            if (System.getProperty("user.dir") == null) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            String basePath = System.getProperty("user.dir") + "/../test-bundle/springboot/target";
            File baseDirectory = new File(basePath);
            if (!baseDirectory.exists()) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            File[] jarFiles =
                    baseDirectory.listFiles((dir, name) -> name.startsWith("springboot-suffix-spanlet-") &&
                                                                     name.endsWith("SNAPSHOT.jar"));
            if (jarFiles.length != 1) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            return jarFiles[0];
        } else {
            throw new IllegalStateException("Could not determine project directories do get bundle jars");
        }
    }

}
