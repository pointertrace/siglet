package io.github.pointertrace.siglet.container.config.siglet.springboot;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.siglet.BundleLoader;
import io.github.pointertrace.siglet.container.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfigParser;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletsConfig;
import org.springframework.boot.loader.launch.Archive;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SpringBootBundleLoader implements BundleLoader {

    private static final String CONFIG_FILE_ENTRY = "BOOT-INF/classes/siglet-config.yaml";

    private final SigletConfigParser sigletConfigParser = new SigletConfigParser();

    public SigletBundle load(File file) {

        if (!file.isFile()) {
            throw new SigletError(String.format("File %s does not exists or is not a file", file.getAbsolutePath()));
        }

        try {

            SpringBootClassLoader classLoader;
            Archive archive = Archive.create(file);
            classLoader = SpringBootClassLoader
                    .of(archive, SpringBootBundleLoader.class.getClassLoader());

            if (classLoader.getResource(CONFIG_FILE_ENTRY) == null) {
                return null;
            }

            String startClassName = SpringBootStartClassReader.read(file);
            SpringBootContextProxy springBootContextProxy = new SpringBootContextProxy(classLoader, startClassName);

            List<SigletDefinition> sigletsDefinitions = new ArrayList<>();
            for(SigletConfig sigletConfig : parseSigletConfig(classLoader).sigletsConfig()) {
                sigletsDefinitions.add(new SpringBootSigletDefinition(springBootContextProxy, sigletConfig));
            }
            springBootContextProxy.start();
            return new SigletBundle(classLoader.getName(),sigletsDefinitions ,springBootContextProxy);

        } catch (Exception e) {
            throw new SigletError(String.format("Error load springBoot uberjar %s. %s",file.getAbsolutePath(),
                    e.getMessage()),e);
        }
    }


    private SigletsConfig parseSigletConfig(ClassLoader classLoader) {

        String sigletConfigFile = read(classLoader.getResourceAsStream(CONFIG_FILE_ENTRY), Path.of("test"));
        return sigletConfigParser.parse(sigletConfigFile);

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

}
