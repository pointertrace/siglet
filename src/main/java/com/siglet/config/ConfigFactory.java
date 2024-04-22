package com.siglet.config;

import com.siglet.SigletError;
import com.siglet.config.item.ConfigItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;

import static com.siglet.config.ConfigCheckFactory.globalConfigChecker;

public class ConfigFactory {

    public Config create(String yaml) {

        ConfigParser configParser = new ConfigParser();

        ConfigNode node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        if (conf instanceof ConfigItem configItem) {
           return new Config(configItem.build());
        } else {
            throw new SigletError("Internal error: config must be a ConfigItem");
        }


    }

    public Config otherCreate(String yaml) {

        ConfigParser configParser = new ConfigParser();

        ConfigNode node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        if (conf instanceof ConfigItem configItem) {
            return new Config(configItem.otherBuild());
        } else {
            throw new SigletError("Internal error: config must be a ConfigItem");
        }


    }
}
