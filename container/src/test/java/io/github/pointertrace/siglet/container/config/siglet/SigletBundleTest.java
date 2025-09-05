package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorContextImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.loader.net.protocol.Handlers;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SigletBundleTest {

    private YamlParser yamlParser;

    private ProtoSpanAdapter protoSpanAdapter;

    @BeforeEach
    void setUp() {
        Handlers.register();
        yamlParser = new YamlParser();

        Span span = Span.newBuilder().setName("name").build();
        protoSpanAdapter = new ProtoSpanAdapter();
        protoSpanAdapter.recycle(span, null, null);
    }


    @Test
    void load_fatjar() throws Exception {

        try(SigletBundle sigletBundle = SigletBundle.load(ExampleJarsInfo.getFatJarExampleSigletFile())) {

            assertNotNull(sigletBundle);
            assertTrue(sigletBundle.id().startsWith("fatjar:"));
            assertTrue(sigletBundle.id().endsWith("fatjar-suffix-spanlet-1.0.0-SNAPSHOT.jar"));

            assertNotNull(sigletBundle.definitions());
            assertEquals(1, sigletBundle.definitions().size());
            assertNotNull(sigletBundle.definitions().getFirst());

            SigletConfig sigletConfig = (sigletBundle.definitions().getFirst().getSigletConfig());
            assertEquals("fatjar-suffix-spanlet", sigletConfig.name());

            assertNotNull(sigletBundle.definitions().getFirst().createProcessor());
            Spanlet<?> spanlet = assertInstanceOf(Spanlet.class,
                    sigletBundle.definitions().getFirst().createProcessor());

            Node config = yamlParser.parse("suffix: -suffix");

            NodeChecker nodeChecker = assertInstanceOf(NodeChecker.class, sigletBundle.definitions().getFirst().createConfigChecker());
            nodeChecker.check(config);

            Object spanletConfig = config.getValue();
            assertNotNull(sigletConfig);

            ProcessorContextImpl<?> processorContext = new ProcessorContextImpl<>(spanletConfig);

            spanlet.span(protoSpanAdapter, (ProcessorContext) processorContext, ResultFactoryImpl.INSTANCE);

            assertEquals("name-suffix-fatjar", protoSpanAdapter.getName());

        }

    }

    @Test
    void load_springBootUberJar() throws Exception {
        try (SigletBundle sigletBundle = SigletBundle.load(ExampleJarsInfo.getSpringBootExampleSigletFile())) {

            assertNotNull(sigletBundle);
            assertTrue(sigletBundle.id().startsWith("springboot-uberjar:"));
            assertTrue(sigletBundle.id().endsWith("springboot-suffix-spanlet-1.0.0-SNAPSHOT.jar"));
            assertNotNull(sigletBundle.definitions());
            assertEquals(1, sigletBundle.definitions().size());
            assertNotNull(sigletBundle.definitions().getFirst());

            SigletConfig sigletConfig = (sigletBundle.definitions().getFirst().getSigletConfig());
            assertEquals("springboot-suffix-spanlet", sigletConfig.name());

            assertNotNull(sigletBundle.definitions().getFirst().createProcessor());
            Spanlet<?> spanlet = assertInstanceOf(Spanlet.class,
                    sigletBundle.definitions().getFirst().createProcessor());

            Node config = yamlParser.parse("suffix: -suffix");

            NodeChecker nodeChecker = assertInstanceOf(NodeChecker.class, sigletBundle.definitions().getFirst().createConfigChecker());
            nodeChecker.check(config);

            Object spanletConfig = config.getValue();
            assertNotNull(sigletConfig);

            ProcessorContextImpl<?> processorContext = new ProcessorContextImpl<>(spanletConfig);

            spanlet.span(protoSpanAdapter, (ProcessorContext) processorContext, ResultFactoryImpl.INSTANCE);

            assertEquals("name-suffix-springboot-uberjar", protoSpanAdapter.getName());

        }
    }
}