package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.config.descriptor.LocatedInetSocketAddress;
import io.github.pointertrace.siglet.impl.config.descriptor.ReceiverDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.receiver.debug.DebugReceiver;
import io.github.pointertrace.siglet.impl.engine.receiver.debug.DebugReceiverType;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OtelGrpcReceiverTypeTest {

    private OtelGrpcReceiverType otelGrpcReceiverType;

    @BeforeEach
    public void setUp() {
        otelGrpcReceiverType = new OtelGrpcReceiverType();
    }

    @Test
    void parser() {
        String config = """
                address: 127.0.0.1:4317
                """;


        Optional<Schema.Builder<?,OtelGrpcReceiverConfig>> optionalBuilder = otelGrpcReceiverType.getConfigurationFactory().createConfigSchema();

        assertTrue(optionalBuilder.isPresent());


        Node node = Parser.DEFAULT.parse(config);

        Schema schema = optionalBuilder.get().build();
        Factory factory = schema.validate(node);

        OtelGrpcReceiverConfig otelGrpcReceiverConfig = factory.create(OtelGrpcReceiverConfig.class);

        assertNotNull(otelGrpcReceiverConfig);
        assertEquals(new java.net.InetSocketAddress("127.0.0.1",4317), otelGrpcReceiverConfig.getAddress().getInetSocketAddress());
        assertEquals(Location.of(1,10), otelGrpcReceiverConfig.getAddress().getLocation());


    }

    @Test
    void create() {

        ReceiverDescriptorMock receiverDescriptorMock = new ReceiverDescriptorMock();
        receiverDescriptorMock.setName(new StringValue("grpc-receiver"));
        receiverDescriptorMock.setType(new StringValue("grpc"));

        OtelGrpcReceiverConfig config = new OtelGrpcReceiverConfig();
        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved("127.0.0.1",4317);

        LocatedInetSocketAddress locatedInetSocketAddress = new LocatedInetSocketAddress();
        locatedInetSocketAddress.setInetSocketAddress(inetSocketAddress);

        config.setAddress(locatedInetSocketAddress);

        receiverDescriptorMock.setConfig(config);

        ReceiverNode receiverNode = new ReceiverNode(receiverDescriptorMock);


        assertInstanceOf(OtelGrpcReceiver.class, otelGrpcReceiverType.getComponentCreator().create(null, receiverNode));
    }

    public static class ReceiverDescriptorMock extends ReceiverDescriptor {

        @Override
        public void setName(StringValue name) {
            super.setName(name);
        }

        @Override
        protected void setType(StringValue type) {
            super.setType(type);
        }

        @Override
        protected void setConfig(Object config) {
            super.setConfig(config);
        }
    }
}