package io.github.pointertrace.siglet.impl;

import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.impl.engine.Context;
import io.github.pointertrace.siglet.impl.engine.ContextFactory;
import io.github.pointertrace.siglet.impl.engine.SigletEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Siglet {

    private static final Logger LOGGER = LoggerFactory.getLogger(Siglet.class);

    private final String configTxt;

    private final List<SigletBundle> sigletBundles;

    private SigletEngine sigletEngine;

    public Siglet(String configTxt) {
        this(configTxt, List.of());
    }

    public Siglet(String configTxt, List<SigletBundle> sigletBundles) {
        this.configTxt = configTxt;
        this.sigletBundles = new ArrayList<>(sigletBundles);

    }

    public void start() {

        LOGGER.info("Starting siglet engine");

        ContextFactory contextFactory = new ContextFactory();

        Context context = contextFactory.create(configTxt, sigletBundles);

        sigletEngine = new SigletEngine(context);

        sigletEngine.start();

    }

    public void stop() {
        sigletEngine.stop();
    }
}
