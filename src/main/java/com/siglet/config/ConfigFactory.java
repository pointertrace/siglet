package com.siglet.config;

import com.siglet.SigletError;
import com.siglet.config.item.ConfigItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;

import static com.siglet.config.ConfigCheckFactory.globalConfigChecker;

public class ConfigFactory {

    public Config create(String yaml) {

        ConfigParser configParser = new ConfigParser();

        Node node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        if (conf instanceof ConfigItem configItem) {
            configItem.afterSetValues();
            return new Config(configItem);
        } else {
            throw new SigletError("Internal error: config must be a ConfigItem");
        }


    }
}
