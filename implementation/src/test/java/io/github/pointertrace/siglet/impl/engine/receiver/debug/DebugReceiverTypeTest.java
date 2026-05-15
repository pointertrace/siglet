package io.github.pointertrace.siglet.impl.engine.receiver.debug;

import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.descriptor.ReceiverDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.receiver.grpc.OtelGrpcReceiverConfig;
import io.github.pointertrace.siglet.impl.engine.receiver.grpc.OtelGrpcReceiverType;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DebugReceiverTypeTest {

    private DebugReceiverType debugReceiverType;

    @BeforeEach
    public void setUp() {
        debugReceiverType = new DebugReceiverType();
    }

    @Test
    void parser() {

        Optional<Schema.Builder<?, Void>> optionalBuilder = debugReceiverType.getConfigurationFactory().createConfigSchema();

        assertTrue(optionalBuilder.isEmpty());

    }

    @Test
    void create() {

        ReceiverDescriptorMock receiverDescriptorMock = new ReceiverDescriptorMock();
        receiverDescriptorMock.setName(new StringValue("debug-receiver"));
        receiverDescriptorMock.setType(new StringValue("debug"));

        ReceiverNode receiverNode = new ReceiverNode(receiverDescriptorMock);



        assertInstanceOf(DebugReceiver.class, debugReceiverType.getComponentCreator().create(null, receiverNode));
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
    }

}