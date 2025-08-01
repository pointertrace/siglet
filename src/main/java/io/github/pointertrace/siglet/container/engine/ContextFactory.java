package io.github.pointertrace.siglet.container.engine;

import io.github.pointertrace.siglet.container.config.Config;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.siglet.SigletConfig;

import java.util.List;

public class ContextFactory {

    public Context create(String configTxt, List<SigletConfig> sigletsConfigs) {
        ConfigFactory configFactory = new ConfigFactory();
        Config config = configFactory.create(configTxt, sigletsConfigs);
        // todo incluir os object pools
        return new Context(config, null, null);
    }
}
