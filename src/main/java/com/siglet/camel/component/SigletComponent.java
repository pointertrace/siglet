package com.siglet.camel.component;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

public class SigletComponent extends DefaultComponent {

    private final GrpcServers grpcServers = new GrpcServers();

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        System.out.println("criando endpoint para uri " + uri);
        return new SigletEndpoint(uri, this, grpcServers);
    }


    @Override
    public void start() {
        System.out.println("component started");
        super.start();
        grpcServers.start();
    }

    @Override
    public void stop() {
        System.out.println("component stoped");
        super.stop();
        grpcServers.stop();
    }


}
