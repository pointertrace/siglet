package com.siglet.config.item;

import com.siglet.SigletError;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class GrpcExporterUriTest {

    private GrpcExporterUri grpcExporterUri;


    @Test
    void toString_noParams() {

        grpcExporterUri = new GrpcExporterUri(InetSocketAddress.createUnresolved("localhost", 500), null, null);


        assertEquals("otelgrpc:localhost:500", grpcExporterUri.toString());

    }

    @Test
    void toString_batchSize() {

        grpcExporterUri = new GrpcExporterUri(InetSocketAddress.createUnresolved("localhost", 500), 100, null);


        assertEquals("otelgrpc:localhost:500?batchSizeInSignals=100", grpcExporterUri.toString());

    }

    @Test
    void toString_batchTimout() {

        grpcExporterUri = new GrpcExporterUri(InetSocketAddress.createUnresolved("localhost", 500), null, 200);


        assertEquals("otelgrpc:localhost:500?batchTimeoutInMillis=200", grpcExporterUri.toString());

    }

    @Test
    void toString_batchSize_batchTimout() {

        grpcExporterUri = new GrpcExporterUri(InetSocketAddress.createUnresolved("localhost", 500), 100, 200);


        assertEquals("otelgrpc:localhost:500?batchSizeInSignals=100&batchTimeoutInMillis=200", grpcExporterUri.toString());

    }

    @Test
    void of() {

        grpcExporterUri = GrpcExporterUri.of("otelgrpc:address:500");

        assertEquals(InetSocketAddress.createUnresolved("address", 500), grpcExporterUri.getAddress());
        assertNull(grpcExporterUri.getBatchSizeInSignals());
        assertNull(grpcExporterUri.getBatchTimeoutInMillis());

    }

    @Test
    void of_batchSizeInSignals() {

        grpcExporterUri = GrpcExporterUri.of("otelgrpc:address:500?batchSizeInSignals=100");

        assertEquals(InetSocketAddress.createUnresolved("address", 500), grpcExporterUri.getAddress());
        assertEquals(100, grpcExporterUri.getBatchSizeInSignals());
        assertNull(grpcExporterUri.getBatchTimeoutInMillis());

    }

    @Test
    void of_batchTimeoutInMillis() {

        grpcExporterUri = GrpcExporterUri.of("otelgrpc:address:500?batchTimeoutInMillis=200");

        assertEquals(InetSocketAddress.createUnresolved("address", 500), grpcExporterUri.getAddress());
        assertNull(grpcExporterUri.getBatchSizeInSignals());
        assertEquals(200, grpcExporterUri.getBatchTimeoutInMillis());

    }

    @Test
    void of_invalidSchema() {

        // invalid schema
        SigletError e = assertThrowsExactly(SigletError.class,
                () -> GrpcExporterUri.of("other:address:500?batchTimeoutInMillis=200"));

        assertEquals("Invalid scheme: other:address:500", e.getMessage());

    }

    @Test
    void of_invalidPort() {
        // invalid address
        Exception e = assertThrowsExactly(SigletError.class,
                () -> GrpcExporterUri.of("otelgrpc:address:xxx?batchTimeoutInMillis=200"));

        assertEquals("Invalid numeric value For input string: \"xxx\"", e.getMessage());

    }

    @Test
    void of_invalidParameter() {
        // invalid parameter
        Exception e = assertThrowsExactly(SigletError.class,
                () -> GrpcExporterUri.of("otelgrpc:address:500?invalidParameter=200"));

        assertEquals("Invalid parameter: invalidParameter=200", e.getMessage());
    }

    @Test
    void of_invalidParameterValue() {
        // invalid parameter value
        Exception e = assertThrowsExactly(SigletError.class,
                () -> GrpcExporterUri.of("otelgrpc:address:500?batchSizeInSignals=xxx"));

        assertEquals("Invalid numeric value For input string: \"xxx\"", e.getMessage());
    }
}