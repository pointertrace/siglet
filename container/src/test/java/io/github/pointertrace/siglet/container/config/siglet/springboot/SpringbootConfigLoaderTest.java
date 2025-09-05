package io.github.pointertrace.siglet.container.config.siglet.springboot;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.config.siglet.ExampleJarsInfo;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.container.config.siglet.fatjar.FatJarSigletBundleLoader;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorContextImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.loader.net.protocol.Handlers;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class SpringbootConfigLoaderTest {

    private SpringBootBundleLoader springBootBundleLoader;

    private File sigletSpringBootFile;

    private ProtoSpanAdapter spanAdapter;

    private YamlParser yamlParser;

    @BeforeEach
    void setUp() {
        Handlers.register();
        sigletSpringBootFile = ExampleJarsInfo.getSpringBootExampleSigletFile();

        springBootBundleLoader = new SpringBootBundleLoader();

        Span span = Span.newBuilder().setName("span-name").build();

        spanAdapter = new ProtoSpanAdapter();
        spanAdapter.recycle(span, null, null);

        yamlParser = new YamlParser();
    }

    @Test
    void load() throws IOException {

        try (SigletBundle sigletBundle = springBootBundleLoader.load(sigletSpringBootFile)) {
            assertTrue(sigletBundle.id().startsWith("springboot-uberjar:"));
            assertTrue(sigletBundle.id().endsWith("springboot-suffix-spanlet-1.0.0-SNAPSHOT.jar"));

            assertEquals(1, sigletBundle.definitions().size());

            SigletDefinition sigletDefinition = sigletBundle.definitions().getFirst();

            assertEquals("springboot-suffix-spanlet", sigletDefinition.getSigletConfig().name());
            assertEquals("adds a suffix to span name", sigletDefinition.getSigletConfig().description());
            assertEquals("io.github.pointertrace.siglet.container.test.bundle.springboot.suffix.siglet.SuffixSpanlet",
                    sigletDefinition.getSigletConfig().sigletClassName());
            assertEquals("io.github.pointertrace.siglet.container.test.bundle.springboot.suffix.parser.SuffixConfigChecker",
                    sigletDefinition.getSigletConfig().configCheckerFactoryClassName());
            Spanlet<Object> spanlet = assertInstanceOf(Spanlet.class, sigletDefinition.createProcessor());
            NodeChecker nodeChecker = assertInstanceOf(NodeChecker.class, sigletDefinition.createConfigChecker());

            Node node = yamlParser.parse("suffix: -suffix");
            nodeChecker.check(node);
            Object sigletConfig = node.getValue();

            ProcessorContext<Object> processorContext = new ProcessorContextImpl<>(sigletConfig);

            spanlet.span(spanAdapter, processorContext, ResultFactoryImpl.INSTANCE);

            assertEquals("span-name-suffix-springboot-uberjar", spanAdapter.getName());

        }
    }

    @Test
    void load_fileNotExists() {

        sigletSpringBootFile = new File("/invalid-file");

        SigletError e = assertThrows(SigletError.class, () -> springBootBundleLoader.load(sigletSpringBootFile));

        assertEquals("File /invalid-file does not exists or is not a file", e.getMessage());
    }

    @Test
    void load_jarIsNotSpringBootUberJar() {

        sigletSpringBootFile =
                new File(CommandLine.class.getProtectionDomain().getCodeSource().getLocation().getFile());

        assertNull(springBootBundleLoader.load(sigletSpringBootFile));
    }
}