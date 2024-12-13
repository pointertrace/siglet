package com.siglet.config.item;

import com.siglet.SigletError;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class GrpcExporterUriTest {

    private GrpcExporterUri grpcExporterUri;


    @Test
    public void toString_noParams() throws Exception {

        grpcExporterUri = new GrpcExporterUri(InetSocketAddress.createUnresolved("localhost", 500), null, null);


        assertEquals("otelgrpc:localhost:500", grpcExporterUri.toString());

    }

    @Test
    public void toString_batchSize() throws Exception {

        grpcExporterUri = new GrpcExporterUri(InetSocketAddress.createUnresolved("localhost", 500), 100, null);


        assertEquals("otelgrpc:localhost:500?batchSizeInSignals=100", grpcExporterUri.toString());

    }

    @Test
    public void toString_batchTimout() throws Exception {

        grpcExporterUri = new GrpcExporterUri(InetSocketAddress.createUnresolved("localhost", 500), null, 200);


        assertEquals("otelgrpc:localhost:500?batchTimeoutInMillis=200", grpcExporterUri.toString());

    }

    @Test
    public void toString_batchSize_batchTimout() throws Exception {

        grpcExporterUri = new GrpcExporterUri(InetSocketAddress.createUnresolved("localhost", 500), 100, 200);


        assertEquals("otelgrpc:localhost:500?batchSizeInSignals=100&batchTimeoutInMillis=200", grpcExporterUri.toString());

    }

    @Test
    public void of() throws Exception {

        grpcExporterUri = GrpcExporterUri.of("otelgrpc:address:500");

        assertEquals(InetSocketAddress.createUnresolved("address", 500), grpcExporterUri.getAddress());
        assertNull(grpcExporterUri.getBatchSizeInSignals());
        assertNull(grpcExporterUri.getBatchTimeoutInMillis());

    }

    @Test
    public void of_batchSizeInSignals() throws Exception {

        grpcExporterUri = GrpcExporterUri.of("otelgrpc:address:500?batchSizeInSignals=100");

        assertEquals(InetSocketAddress.createUnresolved("address", 500), grpcExporterUri.getAddress());
        assertEquals(100, grpcExporterUri.getBatchSizeInSignals());
        assertNull(grpcExporterUri.getBatchTimeoutInMillis());

    }

    @Test
    public void of_batchTimeoutInMillis() throws Exception {

        grpcExporterUri = GrpcExporterUri.of("otelgrpc:address:500?batchTimeoutInMillis=200");

        assertEquals(InetSocketAddress.createUnresolved("address", 500), grpcExporterUri.getAddress());
        assertNull(grpcExporterUri.getBatchSizeInSignals());
        assertEquals(200, grpcExporterUri.getBatchTimeoutInMillis());

    }

    @Test
    public void of_invalidSchema() throws Exception {

        // invalid schema
        SigletError e = assertThrowsExactly(SigletError.class, () -> {
            GrpcExporterUri.of("other:address:500?batchTimeoutInMillis=200");
        });

        assertEquals("Invalid scheme: other:address:500", e.getMessage());

    }

    @Test
    public void of_invalidPort() throws Exception {
        // invalid address
        Exception e = assertThrowsExactly(SigletError.class, () -> {
            GrpcExporterUri.of("otelgrpc:address:xxx?batchTimeoutInMillis=200");
        });
        assertEquals("Invalid numeric value For input string: \"xxx\"", e.getMessage());

    }

    @Test
    public void of_invalidParameter() throws Exception {
        // invalid parameter
        Exception e = assertThrowsExactly(SigletError.class, () -> {
            GrpcExporterUri.of("otelgrpc:address:500?invalidParameter=200");
        });

        assertEquals("Invalid parameter: invalidParameter=200", e.getMessage());
    }

    @Test
    public void of_invalidParameterValue() throws Exception {
        // invalid parameter value
        Exception e = assertThrowsExactly(SigletError.class, () -> {
            GrpcExporterUri.of("otelgrpc:address:500?batchSizeInSignals=xxx");
        });

        assertEquals("Invalid numeric value For input string: \"xxx\"", e.getMessage());
    }
}