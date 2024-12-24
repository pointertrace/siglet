package com.siglet.config.parser;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.SnakeYamlNodeConfigNodeTranslator;
import org.yaml.snakeyaml.Yaml;

import java.io.StringReader;

public class ConfigParser {

    public Node parse(String config) {
        Yaml yaml = new Yaml();
        org.yaml.snakeyaml.nodes.Node node = yaml.compose(new StringReader(config));

        return SnakeYamlNodeConfigNodeTranslator.translate(node);


    }
}
