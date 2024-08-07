package com.siglet.cli;

import com.siglet.camel.component.SigletComponent;
import com.siglet.config.Config;
import com.siglet.config.ConfigFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class SigleletStarter implements Runnable{

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path to config file.", required = true)
    private String filePath;


    @Override
    public void run() {
        try  {
            String configFileContent = new String(Files.readAllBytes(Paths.get(filePath)));

            ConfigFactory configFactory = new ConfigFactory();
            Config config = configFactory.create(configFileContent);

            CountDownLatch countDownLatch = new CountDownLatch(1);
            try (CamelContext camelContext = new DefaultCamelContext()) {
                camelContext.addComponent("otelgrpc", new SigletComponent());
                camelContext.addRoutes(config.getRouteBuilder());
                camelContext.start();
                Runtime.getRuntime().addShutdownHook(new Thread(countDownLatch::countDown));
                countDownLatch.await();
            }
        } catch (Exception e) {
            System.out.println("error starting siglet:"+e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}
