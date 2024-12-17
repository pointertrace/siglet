package com.siglet.camel.component.otelgrpc;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;

public class SigletConsumer extends DefaultConsumer {

    public SigletConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);

        SigletEndpoint sigletEndpoint = (SigletEndpoint) endpoint;
        if (sigletEndpoint.getEndpointUri().contains("?signalType=trace")) {
            sigletEndpoint.getGrpcServers().addServer(sigletEndpoint.getSocketAddress(),
                    new OtelGrpcTraceServiceImpl(this));
        } else if (sigletEndpoint.getEndpointUri().contains("?signalType=metric")) {
            sigletEndpoint.getGrpcServers().addServer(sigletEndpoint.getSocketAddress(),
                    new OtelGrpcMetricsServiceImpl(this));
        }
    }


}
