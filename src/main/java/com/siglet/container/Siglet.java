package com.siglet.container;

import com.siglet.container.config.Config;
import com.siglet.container.config.ConfigFactory;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.config.siglet.SigletConfigLoader;
import com.siglet.container.engine.SigletEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Siglet {

    private static final Logger LOGGER = LoggerFactory.getLogger(Siglet.class);


    private final String configTxt;

    private final List<SigletConfig> sigletsConfigs;

    private SigletEngine sigletEngine;

    public Siglet(String configTxt) {
        this(configTxt, List.of());
    }

    public Siglet(String configTxt, List<SigletConfig> sigletsConfigs) {
        this.configTxt = configTxt;
        this.sigletsConfigs = new ArrayList<>(sigletsConfigs);

    }

    public void start() {

        ConfigFactory configFactory = new ConfigFactory();
        Config config = configFactory.create(configTxt, sigletsConfigs);
        sigletEngine = new SigletEngine(config.getGraph());

        sigletEngine.start();

        LOGGER.info("Siglet started");
    }

    public void stop() {
        sigletEngine.stop();
        LOGGER.info("Siglet stoped");

    }
}
