package com.siglet.cli;

import com.siglet.SigletError;
import com.siglet.camel.component.drop.DropComponent;
import com.siglet.camel.component.otelgrpc.SigletComponent;
import com.siglet.config.Config;
import com.siglet.config.ConfigFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Siglet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigleletStarter.class);

    private final String configTxt;

    public Siglet(String configTxt) {
        this.configTxt = configTxt;
    }

    public void start() {

        LOGGER.info("Starting Siglet");

        ConfigFactory configFactory = new ConfigFactory();
        Config config = configFactory.create(configTxt);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try (CamelContext camelContext = new DefaultCamelContext()) {
            SigletContext.init(() -> camelContext, config.getReceiversUris());
            camelContext.addComponent("otelgrpc", new SigletComponent());
            camelContext.addComponent("drop", new DropComponent());
            camelContext.addRoutes(config.getRouteBuilder());
            camelContext.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Siglet stopping");
                countDownLatch.countDown();
            }));
            LOGGER.info("Siglet started");
            countDownLatch.await();
            camelContext.stop();
            LOGGER.info("Siglet stopped");
        } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
           throw new SigletError("Error starting siglet",e);
        } catch (Exception e) {
            throw new SigletError("Error starting siglet",e);
        }
    }
}
