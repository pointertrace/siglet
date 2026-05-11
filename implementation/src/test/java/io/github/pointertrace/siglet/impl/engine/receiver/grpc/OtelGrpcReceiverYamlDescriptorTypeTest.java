package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtelGrpcReceiverYamlDescriptorTypeTest {

    private OtelGrpcReceiverType otelGrpcReceiverType;

    @BeforeEach
    public void setUp() {
        otelGrpcReceiverType = new OtelGrpcReceiverType();
    }

    @Test
    void getConfigProperties() {
        String config = """
                address: 127.0.0.1:4317
                """;


        Node node = Parser.DEFAULT.parse(config);


//        Schema schema = object(otelGrpcReceiverType.getConfigSupplier()).addProperties(otelGrpcReceiverType.getConfigProperties());
        Schema schema = null;


        Factory factory = schema.validate(node);

        OtelGrpcReceiverConfig otelGrpcReceiverConfig = factory.create(OtelGrpcReceiverConfig.class);

        assertNotNull(otelGrpcReceiverConfig);
        assertEquals(new java.net.InetSocketAddress("127.0.0.1",4317), otelGrpcReceiverConfig.getAddress().getInetSocketAddress());
        assertEquals(Location.of(1,10), otelGrpcReceiverConfig.getAddress().getLocation());


    }
}

