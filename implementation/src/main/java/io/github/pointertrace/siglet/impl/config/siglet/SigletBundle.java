package io.github.pointertrace.siglet.impl.config.siglet;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.siglet.fatjar.FatJarSigletBundleLoader;
import io.github.pointertrace.siglet.impl.config.siglet.springboot.SpringBootBundleLoader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public record SigletBundle(String id, List<SigletDefinition> definitions,
                           Closeable closeable) implements Closeable {

    private static final List<BundleLoader> DEFINITIONS_LOADERS = List.of(
            new FatJarSigletBundleLoader(),
            new SpringBootBundleLoader()
    );

    public static SigletBundle load(File jarFile) {
        for (BundleLoader bundleLoader : DEFINITIONS_LOADERS) {
            SigletBundle sigletBundle = bundleLoader.load(jarFile);
            if (sigletBundle != null) {
                return sigletBundle;
            }
        }
        throw new SigletError(String.format("Jar %s is not valid as a SpringBoot uber jar or a flat jar",
                jarFile.getAbsolutePath()));
    }


    @Override
    public void close() throws IOException {
        try {
            closeable().close();
        } catch (Throwable t) {
            throw new IOException("Error closing sigletDefinitions jar file", t);
        }

    }
}
