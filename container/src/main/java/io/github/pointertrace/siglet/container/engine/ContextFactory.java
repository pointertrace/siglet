package io.github.pointertrace.siglet.container.engine;

import io.github.pointertrace.siglet.container.config.Config;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;

import java.util.List;

public class ContextFactory {

    public Context create(String configTxt, List<SigletBundle> sigletsBundles) {
        ConfigFactory configFactory = new ConfigFactory();
        Config config = configFactory.create(configTxt, sigletsBundles);
        // todo incluir os object pools
        return new Context(config, null, null);
    }
}
