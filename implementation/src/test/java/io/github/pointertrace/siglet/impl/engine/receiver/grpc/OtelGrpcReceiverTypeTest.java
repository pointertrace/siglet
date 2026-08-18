package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.descriptor.LocatedInetSocketAddress;
import io.github.pointertrace.siglet.impl.config.descriptor.ReceiverDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptors;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OtelGrpcReceiverTypeTest {

    private OtelGrpcReceiverType otelGrpcReceiverType;

    private SigletContext sigletContext;

    private Config config;

    @BeforeEach
    public void setUp() {
        otelGrpcReceiverType = new OtelGrpcReceiverType();

        config = mock(Config.class);
        when(config.getQueueSize(any())).thenReturn(1024);

        sigletContext = mock(SigletContext.class);
        when(sigletContext.getInterceptor()).thenReturn(new Interceptors());
        when(sigletContext.getConfig()).thenReturn(config);

    }

    @Test
    void parser() {
        String config = """
                address: 127.0.0.1:4317
                max-inbound-message-size-bytes: 8388608
                flow-control-window-bytes: 2097152
                keep-alive-time-seconds: 25
                """;


        Optional<Schema.Builder<?, OtelGrpcReceiverConfig>> optionalBuilder = otelGrpcReceiverType.getConfigurationFactory().createConfigSchema();

        assertTrue(optionalBuilder.isPresent());


        Node node = Parser.DEFAULT.parse(config);

        Schema schema = optionalBuilder.get().build();
        Factory factory = schema.validate(node);

        OtelGrpcReceiverConfig otelGrpcReceiverConfig = factory.create(OtelGrpcReceiverConfig.class);

        assertNotNull(otelGrpcReceiverConfig);
        assertEquals(new java.net.InetSocketAddress("127.0.0.1", 4317), otelGrpcReceiverConfig.getAddress().getInetSocketAddress());
        assertEquals(Location.of(1, 10), otelGrpcReceiverConfig.getAddress().getLocation());
        assertEquals(8388608, otelGrpcReceiverConfig.getMaxInboundMessageSizeBytes().getValue().intValue());
        assertEquals(2097152, otelGrpcReceiverConfig.getFlowControlWindowBytes().getValue().intValue());
        assertEquals(25, otelGrpcReceiverConfig.getKeepAliveTimeSeconds().getValue().intValue());


    }

    @Test
    void create() {

        ReceiverDescriptorMock receiverDescriptorMock = new ReceiverDescriptorMock();
        receiverDescriptorMock.setName(new StringValue("grpc-receiver"));
        receiverDescriptorMock.setType(new StringValue("grpc"));

        OtelGrpcReceiverConfig config = new OtelGrpcReceiverConfig();
        config.setQueueSize(new IntegerValue(1024));
        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved("127.0.0.1", 4317);

        LocatedInetSocketAddress locatedInetSocketAddress = new LocatedInetSocketAddress(inetSocketAddress, Location.of(1, 10));

        config.setAddress(locatedInetSocketAddress);

        receiverDescriptorMock.setConfig(config);

        ReceiverNode receiverNode = new ReceiverNode(receiverDescriptorMock);


        assertInstanceOf(OtelGrpcReceiver.class, otelGrpcReceiverType.getComponentCreator()
                .create(sigletContext, receiverNode));
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