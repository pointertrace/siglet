package io.github.pointertrace.siglet.impl.config.siglet;

import java.io.File;

public class ExampleJarsInfo {


    public static File getFatJarExampleSigletFile() {
        if (System.getProperty("user.dir") == null) {
            throw new IllegalStateException("Could not determine project directories do get bundle jars");
        }
        String path = System.getProperty("user.dir") + "/../test-bundle/fatjar/target/fatjar-suffix-spanlet-test.jar";
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalStateException("Could not determine project directories do get bundle jars");
        }
        return file;
    }


public static File getSpringBootExampleSigletFile() {
    if (System.getProperty("user.dir") == null) {
        throw new IllegalStateException("Could not determine project directories do get bundle jars");
    }
    String path = System.getProperty("user.dir") + "/../test-bundle/springboot/target/springboot-suffix-spanlet-test.jar";
    File file = new File(path);
    if (!file.exists()) {
        throw new IllegalStateException("Could not determine project directories do get bundle jars");
    }
    return file;
}

            }
