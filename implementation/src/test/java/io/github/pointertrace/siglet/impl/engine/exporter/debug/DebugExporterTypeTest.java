package io.github.pointertrace.siglet.impl.engine.exporter.debug;

import io.github.pointertrace.siglet.impl.config.descriptor.ExporterDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptors;
import io.github.pointertrace.siglet.parser.Schema;
import io.github.pointertrace.siglet.parser.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DebugExporterTypeTest {

    private DebugExporterType debugExporterType;

    private SigletContext sigletContext;

    @BeforeEach
    public void setUp() {

        debugExporterType = new DebugExporterType();
        sigletContext = mock(SigletContext.class);
        when(sigletContext.getInterceptor()).thenReturn(new Interceptors());
    }

    @Test
    void parser() {

        Optional<Schema.Builder<?, Void>> optionalBuilder = debugExporterType.getConfigurationFactory().createConfigSchema();

        assertTrue(optionalBuilder.isEmpty());

    }

    @Test
    void create() {

        ExporterDescriptorMock exporterDescriptorMock = new ExporterDescriptorMock();
        exporterDescriptorMock.setName(new StringValue("debug-exporter"));
        exporterDescriptorMock.setType(new StringValue("debug"));

        ExporterNode exporterNode = new ExporterNode(exporterDescriptorMock);



        assertInstanceOf(DebugExporter.class, debugExporterType.getComponentCreator().create(sigletContext, exporterNode));
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
    }

}