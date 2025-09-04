package io.github.pointertrace.siglet.container.config.siglet.springboot;

import io.github.pointertrace.siglet.container.SigletError;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class SpringBootStartClassReader {

    private SpringBootStartClassReader() {
    }

    public static String read(File jar) {

        try (JarFile jf = new JarFile(jar)) {
            Manifest mf = jf.getManifest();
            if (mf == null) {
                return null;
            }
            Attributes attrs = mf.getMainAttributes();
            String startClassName = attrs.getValue("Start-Class");
            if (startClassName == null || startClassName.isBlank()) {
                throw new IllegalStateException(String.format("Start-Class not found in MANIFEST.MF in springBoot uberjar" +
                                                              " %s", jar.getAbsolutePath()));
            }
            return startClassName;
        } catch (IOException e) {
            throw new SigletError(String.format("Error reading manifest from springBoot uberjar file %s. %s",
                    jar.getAbsolutePath(), e.getMessage()), e);
        }
    }
}
