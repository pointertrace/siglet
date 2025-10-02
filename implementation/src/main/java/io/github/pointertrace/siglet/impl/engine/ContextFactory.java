package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.ConfigFactory;
import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;

import java.util.List;

public class ContextFactory {

    public Context create(String configTxt, List<SigletBundle> sigletsBundles) {
        ConfigFactory configFactory = new ConfigFactory();
        Config config = configFactory.create(configTxt, sigletsBundles);
        // todo incluir os object pools
        return new Context(config, null, null);
    }
}
