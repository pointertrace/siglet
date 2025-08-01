package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletClassLoader;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfigParser;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class SigletConfigLoader {

    private final SigletConfigParser sigletConfigParser = new SigletConfigParser();

    public SigletsConfig load(Path path) {
        SigletClassLoader classLoader = getClassLoader(path);

        String yaml = read(classLoader.getSigletConfigInputStream(), path);

        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            return sigletConfigParser.parse(yaml, classLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    private String read(InputStream inputStream, Path path) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new SigletError(String.format("Error reading siglet-config.yaml from path %s", path));
        }
    }

    private SigletClassLoader getClassLoader(Path path) {
        try {
            URL jarUrl = path.toUri().toURL();
            return new SigletClassLoader(new File(jarUrl.toURI()), Thread.currentThread().getContextClassLoader());
        } catch (URISyntaxException | IOException e) {
            throw new SigletError(String.format("Error trying to read %s as siglet jar: %s", path, e.getMessage()), e);
        }
    }
}
