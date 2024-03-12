package com.siglet.config;

import com.siglet.config.factory.GrpcReceiverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GrpcReceiverFactoryTest {


    private GrpcReceiverFactory grpcReceiverFactory;


    @BeforeEach
    public void setUp() {
        grpcReceiverFactory = new GrpcReceiverFactory();
    }


    @Test
    public void create() {
        String yaml = """
        name: grpc-name
        address: localhost:8080
        """;

        GrpcReceiver grpcReceiver = grpcReceiverFactory.create(new Yaml().load(yaml));

        assertNotNull(grpcReceiver);
        assertEquals("grpc-name", grpcReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), grpcReceiver.getAddress());


    }
}