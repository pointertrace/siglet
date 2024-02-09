package com.siglet.camel.component;

import io.grpc.Server;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class SigletConsumer extends DefaultConsumer {

    private InetSocketAddress socketAddress;

    private Server server;

    public SigletConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        try {
            var builder = NettyServerBuilder
                    .forAddress(((SigletEndpoint) getEndpoint()).getSocketAddress())
                    .addService(new OtelGrpcTraceServiceImpl(this));
            server = builder.build();
            server.start();
        } catch (RuntimeException e) {
            System.out.println("error:" + e);
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (server != null) {
            try {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                server.shutdownNow();
            }
        }
    }
}
