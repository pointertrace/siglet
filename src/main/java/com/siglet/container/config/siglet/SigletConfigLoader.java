package com.siglet.container.config.siglet;

import com.siglet.SigletError;
import com.siglet.container.config.siglet.parser.ParentLastURLClassLoader;
import com.siglet.container.config.siglet.parser.SigletConfigParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class SigletConfigLoader {

    private final SigletConfigParser sigletConfigParser = new SigletConfigParser();

    public SigletConfig load(Path path) {
        ClassLoader classLoader = getClassLoader(path);
        String yaml = read(classLoader.getResourceAsStream("siglet-config.yaml"), path);

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

    private ClassLoader getClassLoader(Path path) {
        try {
            URL jarUrl = path.toUri().toURL();
            return new ParentLastURLClassLoader(new URL[]{jarUrl}, Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            throw new SigletError(String.format("Error trying to read %s as siglet jar: %s", path, e.getMessage()), e);
        }
    }


}
