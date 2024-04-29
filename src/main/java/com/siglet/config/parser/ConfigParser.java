package com.siglet.config.parser;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.SnakeYamlNodeConfigNodeTranslator;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

import java.io.StringReader;

public class ConfigParser {

    public ConfigNode parse(String config) {
        Yaml yaml = new Yaml();
        Node node = yaml.compose(new StringReader(config));

        return SnakeYamlNodeConfigNodeTranslator.translate(node);


    }
}
