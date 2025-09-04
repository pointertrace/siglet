package io.github.pointertrace.siglet.container.config.siglet.fatjar;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.siglet.BundleLoader;
import io.github.pointertrace.siglet.container.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfigParser;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletsConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarFile;

public class FatJarSigletBundleLoader implements BundleLoader {

    private static final String CONFIG_FILE_ENTRY = "META-INF/siglet-config.yaml";

    private final SigletConfigParser sigletConfigParser = new SigletConfigParser();

    public SigletBundle load(File file) {

        if (! file.isFile()) {
            throw new SigletError(String.format("File %s does not exist or is not a file", file.getAbsolutePath()));
        }
        JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            throw new SigletError(String.format("Error reading %s as a jar file", file.getAbsolutePath()));
        }
        if (jarFile.getJarEntry(CONFIG_FILE_ENTRY) == null) {
            return null;
        } else {
            List<SigletDefinition> sigletsDefinitions = new ArrayList<>();
            FatJarClassLoader fatJarClassLoader = new FatJarClassLoader(jarFile, Thread.currentThread().getContextClassLoader());
            String sigletConfigFile = getSigletConfigFile(fatJarClassLoader);
            SigletsConfig sigletsConfig = sigletConfigParser.parse(sigletConfigFile);
            for (SigletConfig sigletConfig : sigletsConfig.sigletsConfig()) {
                sigletsDefinitions.add(new FatJarSigletDefinition(fatJarClassLoader, sigletConfig));
            }

            return new SigletBundle("fatjar:" + jarFile.getName(), Collections.unmodifiableList(sigletsDefinitions),
                    jarFile);
        }
    }

    private String getSigletConfigFile(ClassLoader classLoader) {

        try (InputStream is = classLoader.getResourceAsStream(CONFIG_FILE_ENTRY)) {
            if (is == null) {
                throw new SigletError(String.format("Could not find %s in jar file", CONFIG_FILE_ENTRY));
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SigletError("Error reading %s in jar file.", e);
        }
    }
}
