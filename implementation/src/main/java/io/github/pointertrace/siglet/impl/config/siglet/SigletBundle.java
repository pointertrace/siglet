package io.github.pointertrace.siglet.impl.config.siglet;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.siglet.fatjar.FatJarBundleLoader;
import io.github.pointertrace.siglet.impl.config.siglet.springboot.SpringBootBundleLoader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class SigletBundle implements Closeable {

    private static final List<BundleLoader> DEFINITIONS_LOADERS = List.of(
            new FatJarBundleLoader(),
            new SpringBootBundleLoader()
    );

    private final String id;
    private final List<? extends SigletDefinition> definitions;
    private final Closeable closeable;

    public SigletBundle(String id, List<? extends SigletDefinition> definitions,
                        Closeable closeable) {
        this.id = id;
        this.definitions = definitions;
        this.closeable = closeable;
    }

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

    public String getId() {
        return id;
    }

    public List<? extends SigletDefinition> getDefinitions() {
        return definitions;
    }

    public Closeable closeable() {
        return closeable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SigletBundle) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.definitions, that.definitions) &&
                Objects.equals(this.closeable, that.closeable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, definitions, closeable);
    }

    @Override
    public String toString() {
        return "SigletBundle[" +
                "id=" + id + ", " +
                "definitions=" + definitions + ", " +
                "closeable=" + closeable + ']';
    }

}
