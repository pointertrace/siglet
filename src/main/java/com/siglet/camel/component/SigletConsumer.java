package com.siglet.camel.component;

import io.grpc.Server;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;

public class SigletConsumer extends DefaultConsumer {

    private Server server;

    public SigletConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
        System.out.println("consumer criado uri " + endpoint.getEndpointUri());

        SigletEndpoint sigletEndpoint = (SigletEndpoint) endpoint;
        if (sigletEndpoint.getEndpointUri().contains("?signalType=trace")) {
            sigletEndpoint.getGrpcServers().addServer(sigletEndpoint.getSocketAddress(),
                    new OtelGrpcTraceServiceImpl(this));
        } else if (sigletEndpoint.getEndpointUri().contains("?signalType=metric")) {
            sigletEndpoint.getGrpcServers().addServer(sigletEndpoint.getSocketAddress(),
                    new OtelGrpcMetricsServiceImpl(this));
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        System.out.println("depois iniciado");
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        System.out.println("depois finalizado");
    }
}
