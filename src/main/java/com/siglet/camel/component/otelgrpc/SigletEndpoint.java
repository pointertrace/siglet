package com.siglet.camel.component.otelgrpc;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.util.URISupport;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class SigletEndpoint extends DefaultEndpoint {

    private final InetSocketAddress socketAddress;

    private final GrpcServers grpcServers;

    private String signalType;

    private int batchSizeInSignals;

    public SigletEndpoint(String uri, Component component, GrpcServers grpcServers) {
        super(uri, component);
        this.grpcServers = grpcServers;
        socketAddress = SigletEndpoint.getSocketAddress(uri);
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public GrpcServers getGrpcServers() {
        return grpcServers;
    }

    @Override
    public Producer createProducer() {
        return new SigletProducer(this);
    }

    @Override
    public String getEndpointUri() {
        return super.getEndpointUri();
    }

    @Override
    public Consumer createConsumer(Processor processor) {
        return new SigletConsumer(this, processor);
    }

    @Override
    protected String createEndpointUri() {
        return super.createEndpointUri();
    }

    // Testar!!!!!
    public static InetSocketAddress getSocketAddress(String uri) {
        String hostPort = null;
        try {
            hostPort = URISupport.extractRemainderPath(new URI(uri), true);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String[] parts = hostPort.split(":");

        return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));

    }

    public void setSignalType(String value) {
        this.signalType = signalType;
    }

    public String getSignalType() {
        return signalType;
    }

    public void setBatchSizeInSignals(int batchSizeInSignals) {
       this.batchSizeInSignals = batchSizeInSignals;
    }

    public int getBatchSizeInSignals() {
        return batchSizeInSignals;
    }

}
