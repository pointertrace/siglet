package com.siglet.container.config.siglet.parser;


import com.siglet.parser.Node;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.parser.YamlParser;

public class SigletConfigParser {


    private final YamlParser yamlParser = new YamlParser();

    private final SigletConfigCheckerFactory sigletConfigCheckerFactory = new SigletConfigCheckerFactory();

    public SigletConfig parse(String yaml) {
       return parse(yaml, Thread.currentThread().getContextClassLoader());
    }

    public SigletConfig parse(String yaml, ClassLoader classLoader) {

        Node sigletNode = yamlParser.parse(yaml);

        sigletConfigCheckerFactory.create(classLoader).check(sigletNode);

        return sigletNode.getValue(SigletConfig.class);
    }
}
