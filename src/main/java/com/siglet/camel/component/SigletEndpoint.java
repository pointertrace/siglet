package com.siglet.camel.component;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.util.InetAddressUtil;
import org.apache.camel.util.URISupport;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class SigletEndpoint extends DefaultEndpoint {

    private final InetSocketAddress socketAddress;

    public SigletEndpoint(String uri, Component component) {

        super(uri, component);
        socketAddress = SigletEndpoint.getSocketAddress(uri);
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
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
}
