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
        SigletBundle sigletBundle = SigletBundle.load(ExampleJarsInfo.getFatJarExampleSigletFile());

        assertNotNull(sigletBundle);
        assertTrue(sigletBundle.id().startsWith("fatjar:"));
        assertTrue(sigletBundle.id().endsWith("fatjar-suffix-spanlet-1.0.0-SNAPSHOT.jar"));

        assertNotNull(sigletBundle.definitions());
        assertEquals(1, sigletBundle.definitions().size());
        assertNotNull(sigletBundle.definitions().getFirst());

        SigletConfig sigletConfig = (sigletBundle.definitions().getFirst().getSigletConfig());
        assertEquals("suffix-spanlet", sigletConfig.name());

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

        sigletBundle.close();

    }

    @Test
    void test() {
        System.out.println(System.getProperty("user.dir") + "/../test-bundle/fatjar/target");

        String path = System.getProperty("user.dir") + "/../test-bundle/fatjar/target";

        File ff = new File(path);
        System.out.println(Arrays.toString(ff.list((f, n) -> n.startsWith("fatjar-suffix-spanlet-") && n.endsWith("SNAPSHOT.jar"))));

        System.out.println(System.getProperty("project.build.directory"));
    }

    @Test
    void test1() {

        if (System.getProperty("project.build.directory") == null) {
            if (System.getProperty("user.dir") == null) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            String basePath = System.getProperty("user.dir") + "/../test-bundle/fatjar/target";
            File baseDirectory = new File(basePath);
            if (!baseDirectory.exists()) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            File[] jarFiles = baseDirectory.listFiles((dir, name) -> name.startsWith("fatjar-suffix-spanlet-") &&
                                                                     name.endsWith("SNAPSHOT.jar"));
            if (jarFiles.length != 1) {
                throw new IllegalStateException("Could not determine project directories do get bundle jars");
            }
            System.out.println(jarFiles[0]);
        }
    }

    @Test
    void load_springBootUberJar() throws Exception {
        SigletBundle sigletBundle =
                SigletBundle.load(ExampleJarsInfo.getSpringBootExampleSigletFile());

        assertNotNull(sigletBundle);
        assertTrue(sigletBundle.id().startsWith("springboot-uberjar:"));
        assertTrue(sigletBundle.id().endsWith("springboot-suffix-spanlet-1.0.0-SNAPSHOT.jar"));
        assertNotNull(sigletBundle.definitions());
        assertEquals(1, sigletBundle.definitions().size());
        assertNotNull(sigletBundle.definitions().getFirst());

        SigletConfig sigletConfig = (sigletBundle.definitions().getFirst().getSigletConfig());
        assertEquals("suffix-spanlet", sigletConfig.name());

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

        sigletBundle.close();

    }
}