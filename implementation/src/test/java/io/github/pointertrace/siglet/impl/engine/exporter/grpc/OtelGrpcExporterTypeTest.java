package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.descriptor.ExporterDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.LocatedInetSocketAddress;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptors;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OtelGrpcExporterTypeTest {

    private OtelGrpcExporterType otelGrpcExporterType;

    private SigletContext context;

    private Config config;

    @BeforeEach
    public void setUp() {
        otelGrpcExporterType = new OtelGrpcExporterType();

        context =  mock(SigletContext.class);
        when(context.getInterceptor()).thenReturn(new Interceptors());
        config = mock(Config.class);
        when(context.getConfig()).thenReturn(config);
    }

    @Test
    void parser() {
        String yaml = """
                address: 127.0.0.1:4317
                """;


        Optional<Schema.Builder<?, OtelGrpcExporterConfig>> optionalBuilder = otelGrpcExporterType.getConfigurationFactory().createConfigSchema();

        assertTrue(optionalBuilder.isPresent());


        Node node = Parser.DEFAULT.parse(yaml);

        Schema schema = optionalBuilder.get().build();
        Factory factory = schema.validate(node);

        OtelGrpcExporterConfig otelGrpcExporterConfig = factory.create(OtelGrpcExporterConfig.class);

        assertNotNull(otelGrpcExporterConfig);
        assertEquals(new java.net.InetSocketAddress("127.0.0.1",4317), otelGrpcExporterConfig.getAddress().getInetSocketAddress());
        assertEquals(Location.of(1,10), otelGrpcExporterConfig.getAddress().getLocation());


    }

    @Test
    void parser_all() {
        String yaml = """
                address: 127.0.0.1:4317
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                queue-size: 3
                """;


        Optional<Schema.Builder<?, OtelGrpcExporterConfig>> optionalBuilder = otelGrpcExporterType.getConfigurationFactory().createConfigSchema();

        assertTrue(optionalBuilder.isPresent());


        Node node = Parser.DEFAULT.parse(yaml);

        Schema schema = optionalBuilder.get().build();
        Factory factory = schema.validate(node);

        OtelGrpcExporterConfig otelGrpcExporterConfig = factory.create(OtelGrpcExporterConfig.class);

        assertNotNull(otelGrpcExporterConfig);
        assertEquals(new java.net.InetSocketAddress("127.0.0.1",4317), otelGrpcExporterConfig.getAddress().getInetSocketAddress());
        assertEquals(Location.of(1,10), otelGrpcExporterConfig.getAddress().getLocation());

        assertEquals(1, otelGrpcExporterConfig.getBatchSizeInSignals().getValue().intValue());
        assertEquals(Location.of(2,24), otelGrpcExporterConfig.getBatchSizeInSignals().getLocation());

        assertEquals(2, otelGrpcExporterConfig.getBatchTimeoutInMillis().getValue().intValue());
        assertEquals(Location.of(3,26), otelGrpcExporterConfig.getBatchTimeoutInMillis().getLocation());

        assertEquals(3, otelGrpcExporterConfig.getQueueSize().getValue().intValue());
        assertEquals(Location.of(4,13), otelGrpcExporterConfig.getQueueSize().getLocation());
    }

    @Test
    void create() {

        ExporterDescriptorMock exporterDescriptorMock = new ExporterDescriptorMock();
        exporterDescriptorMock.setName(new StringValue("grpc-exporter"));
        exporterDescriptorMock.setType(new StringValue("grpc"));

        OtelGrpcExporterConfig config = new OtelGrpcExporterConfig();
        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved("127.0.0.1",4317);

        LocatedInetSocketAddress locatedInetSocketAddress = new LocatedInetSocketAddress(inetSocketAddress, Location.of(1,10));

        config.setAddress(locatedInetSocketAddress);

        exporterDescriptorMock.setConfig(config);

        ExporterNode exporterNode = new ExporterNode(exporterDescriptorMock);


        assertInstanceOf(OtelGrpcExporter.class, otelGrpcExporterType.getComponentCreator().create(context, exporterNode));
    }

    public static class ExporterDescriptorMock extends ExporterDescriptor {

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