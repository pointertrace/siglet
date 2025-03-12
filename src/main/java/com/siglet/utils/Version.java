package com.siglet.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Version {

    private static final Logger LOGGER = LoggerFactory.getLogger(Version.class);

    private static String version;

    public static String get() {
        if (version == null){
            Properties properties = new Properties();
            try {
                properties.load(Version.class.getClassLoader().getResourceAsStream("version.properties"));
                version = properties.getProperty("version");
            } catch (IOException e) {
                LOGGER.error("internal error getting version",e);
                return "undetermined";
            }
        }
        return version;
    }
}
