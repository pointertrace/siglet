package com.siglet.container.engine;

import com.siglet.container.config.Config;
import com.siglet.container.config.ConfigFactory;
import com.siglet.container.config.graph.Graph;
import com.siglet.container.config.raw.EventLoopConfig;
import com.siglet.container.config.siglet.SigletConfig;

import java.util.List;

public class Context {

    private final Config config;

    private static final EventLoopConfig defaultEventLoopConfig =
            EventLoopConfig.of(1_000, Runtime.getRuntime().availableProcessors());

    private final EventLoopConfig globalEventLoopConfig;

    public Context(String configTxt, List<SigletConfig> sigletsConfigs) {
        ConfigFactory configFactory = new ConfigFactory();
        this.config = configFactory.create(configTxt, sigletsConfigs);
        globalEventLoopConfig = defaultEventLoopConfig.chain(config.getGlobalConfig());
    }

    public Graph getGraph() {
        return config.getGraph();
    }

    public EventLoopConfig getEventLoopConfig(Object specificConfig) {
        return globalEventLoopConfig.chain(EventLoopConfig.of(specificConfig));
    }

}
