package com.siglet.misc;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
public class MultipleRoutes {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("file:input1")
                        .to("log:route1")
                        .to("file:output");

                from("file:input2")
                        .to("log:route2")
                        .to("file:output");

                from("file:input3")
                        .to("log:route3")
                        .to("file:output");
            }
        });

        context.start();
        Thread.sleep(5000);
        context.stop();
    }
}
