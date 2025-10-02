package io.github.pointertrace.siglet.impl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class VersionRetrievers {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionRetrievers.class);

    private static String version;

    private VersionRetrievers() {
    }

    public static String get() {
        if (version == null){
            Properties properties = new Properties();
            try {
                properties.load(VersionRetrievers.class.getClassLoader().getResourceAsStream("version.properties"));
                version = properties.getProperty("version");
            } catch (IOException e) {
                LOGGER.error("internal error getting version",e);
                return "undetermined";
            }
        }
        return version;
    }
}
