package io.github.pointertrace.siglet.container.config.siglet.parser;


import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;

public class SigletConfigParser {


    private final YamlParser yamlParser = new YamlParser();

    private final SigletConfigCheckerFactory sigletConfigCheckerFactory = new SigletConfigCheckerFactory();


    public SigletsConfig parse(String yaml) {

        Node sigletNode = yamlParser.parse(yaml);

        sigletConfigCheckerFactory.create().check(sigletNode);

        return sigletNode.getValue(SigletsConfig.class);
    }
}
