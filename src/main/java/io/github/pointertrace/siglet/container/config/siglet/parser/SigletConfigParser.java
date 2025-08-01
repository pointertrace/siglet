package io.github.pointertrace.siglet.container.config.siglet.parser;


import io.github.pointertrace.siglet.container.config.siglet.SigletsConfig;
import io.github.pointertrace.siget.parser.Node;
import io.github.pointertrace.siget.parser.YamlParser;

public class SigletConfigParser {


    private final YamlParser yamlParser = new YamlParser();

    private final SigletConfigCheckerFactory sigletConfigCheckerFactory = new SigletConfigCheckerFactory();

    public SigletsConfig parse(String yaml) {
        return parse(yaml, Thread.currentThread().getContextClassLoader());
    }

    public SigletsConfig parse(String yaml, ClassLoader classLoader) {

        Node sigletNode = yamlParser.parse(yaml);

        sigletConfigCheckerFactory.create(classLoader).check(sigletNode);

        return sigletNode.getValue(SigletsConfig.class);
    }
}
