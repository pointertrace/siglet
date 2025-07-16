package com.siglet.container.engine;

import com.siglet.container.config.Config;
import com.siglet.container.config.ConfigFactory;
import com.siglet.container.config.raw.EventLoopConfig;
import com.siglet.container.config.siglet.SigletConfig;

import java.util.List;

public class ContextFactory {

    public Context create(String configTxt, List<SigletConfig> sigletsConfigs) {
        ConfigFactory configFactory = new ConfigFactory();
        Config config = configFactory.create(configTxt, sigletsConfigs);
        // todo incluir os object pools
        return new Context(config, null, null,
                EventLoopConfig.defaultConfig().chain(config.getGlobalConfig()));
    }
}
