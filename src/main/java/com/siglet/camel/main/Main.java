package com.siglet.camel.main;

import com.siglet.camel.component.SigletComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.example.MyRouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) throws Exception {


        CountDownLatch countDownLatch = new CountDownLatch(1);
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("otelgrpc", new SigletComponent());
        camelContext.addRoutes(new MyRouteBuilder());
        camelContext.start();

        Runtime.getRuntime().addShutdownHook(new Thread(countDownLatch::countDown));

        countDownLatch.await();


    }

}
