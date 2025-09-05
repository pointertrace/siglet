package io.github.pointertrace.siglet.parser;

import io.github.pointertrace.siglet.parser.node.SnakeYamlNodeConfigNodeTranslator;
import org.yaml.snakeyaml.Yaml;

import java.io.StringReader;

public class YamlParser {

    public Node parse(String config) {
        Yaml yaml = new Yaml();
        org.yaml.snakeyaml.nodes.Node node = yaml.compose(new StringReader(config));

        return SnakeYamlNodeConfigNodeTranslator.translate(node);


    }
}
