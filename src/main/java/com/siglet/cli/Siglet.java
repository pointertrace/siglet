package com.siglet.cli;

import com.siglet.camel.component.drop.DropComponent;
import com.siglet.camel.component.otelgrpc.SigletComponent;
import com.siglet.config.Config;
import com.siglet.config.ConfigFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.concurrent.CountDownLatch;

public class Siglet {

    private final String configTxt;

    public Siglet(String configTxt) {
        this.configTxt = configTxt;
    }

    public void start() {

        ConfigFactory configFactory = new ConfigFactory();
        Config config = configFactory.create(configTxt);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try (CamelContext camelContext = new DefaultCamelContext()) {
            SigletContext.init(() -> camelContext, config.getReceiversUris());
            camelContext.addComponent("otelgrpc", new SigletComponent());
            camelContext.addComponent("drop", new DropComponent());
            camelContext.addRoutes(config.getRouteBuilder());
            camelContext.start();
            Runtime.getRuntime().addShutdownHook(new Thread(countDownLatch::countDown));
            countDownLatch.await();
            camelContext.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
