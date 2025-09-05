package io.github.pointertrace.siglet.container.config.siglet.fatjar;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.config.siglet.ExampleJarsInfo;
import io.github.pointertrace.siglet.container.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorContextImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.*;

class FatJarSigletBundleLoaderTest {

    private FatJarSigletBundleLoader fatJarSigletBundleLoader;

    private File sigletJarFile;

    private ProtoSpanAdapter spanAdapter;

    private YamlParser yamlParser;

    @BeforeEach
    void setUp() {

        sigletJarFile = ExampleJarsInfo.getFatJarExampleSigletFile();

        fatJarSigletBundleLoader = new FatJarSigletBundleLoader();

        Span span = Span.newBuilder().setName("span-name").build();

        spanAdapter = new ProtoSpanAdapter();
        spanAdapter.recycle(span, null, null);

        yamlParser = new YamlParser();
    }

    @Test
    void load() throws IOException {

        SigletBundle sigletBundle = fatJarSigletBundleLoader.load(sigletJarFile);

        assertEquals("fatjar:" + new JarFile(sigletJarFile).getName(),sigletBundle.id());

        assertEquals(1, sigletBundle.definitions().size());

        SigletDefinition sigletDefinition = sigletBundle.definitions().getFirst();

        assertEquals("fatjar-suffix-spanlet", sigletDefinition.getSigletConfig().name());
        assertEquals("adds a suffix to span name", sigletDefinition.getSigletConfig().description());
        assertEquals("io.github.pointertrace.siglet.container.test.bundle.jatjar.suffix.siglet.SuffixSpanlet",
                sigletDefinition.getSigletConfig().sigletClassName());
        assertEquals("io.github.pointertrace.siglet.container.test.bundle.jatjar.suffix.parser.SuffixConfigCheckerFactory",
                sigletDefinition.getSigletConfig().configCheckerFactoryClassName());
        Spanlet<Object> spanlet = assertInstanceOf(Spanlet.class, sigletDefinition.createProcessor());
        NodeChecker nodeChecker = assertInstanceOf(NodeChecker.class, sigletDefinition.createConfigChecker());

        Node node = yamlParser.parse("suffix: -suffix");
        nodeChecker.check(node);
        Object sigletConfig = node.getValue();

        ProcessorContext<Object> processorContext = new ProcessorContextImpl<>(sigletConfig);

        spanlet.span(spanAdapter, processorContext, ResultFactoryImpl.INSTANCE);

        assertEquals("span-name-suffix-fatjar", spanAdapter.getName());

    }

    @Test
    void load_fileNotExists() {

        sigletJarFile = new File("/invalid-file");

        SigletError e = assertThrows(SigletError.class,() -> fatJarSigletBundleLoader.load(sigletJarFile));

        assertEquals("File /invalid-file does not exist or is not a file", e.getMessage());
    }

    @Test
    void load_jarIsNotSigletFatJar() {

        sigletJarFile =
                new File(CommandLine.class.getProtectionDomain().getCodeSource().getLocation().getFile());

        assertNull(fatJarSigletBundleLoader.load(sigletJarFile));
    }
}