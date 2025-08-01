package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siget.example.simplesuffixspanlet.siglet.SuffixSpanlet;
import io.github.pointertrace.siget.parser.NodeChecker;
import io.github.pointertrace.siglet.api.Processor;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorConfigLoaderTest {

    private SigletConfigLoader sigletConfigLoader;

    private URL spanletUrl;

    @BeforeEach
    void setUp() {

        sigletConfigLoader = new SigletConfigLoader();

        spanletUrl = SuffixSpanlet.class.getProtectionDomain().getCodeSource().getLocation();

    }

    @Test
    void load() throws Exception {

        SigletsConfig sigletsConfig = sigletConfigLoader.load(Paths.get(spanletUrl.toURI()));

        assertNotNull(sigletsConfig);
        assertEquals(1, sigletsConfig.sigletsConfig().size());

        SigletConfig sigletConfig = sigletsConfig.sigletsConfig().getFirst();

        assertEquals("suffix-spanlet", sigletConfig.name());
        assertEquals("adds a span name suffix", sigletConfig.description());
        assertInstanceOf(Class.class, sigletConfig.sigletClass());
        assertTrue(Processor.class.isAssignableFrom(sigletConfig.sigletClass()));
        assertInstanceOf(NodeChecker.class, sigletConfig.configChecker());

        Object sigletInstance = sigletConfig.createSigletInstance();
        Method spanMethod = sigletConfig.getSigletMethod();

        Span protoSpan = Span.newBuilder().setName("span-name").build();

        ProtoSpanAdapter span = new ProtoSpanAdapter().recycle(protoSpan, null, null);

        spanMethod.invoke(sigletInstance, span, null, ResultFactoryImpl.INSTANCE);


        assertEquals("0:span-name", span.getName());

        Method getClassToCheckMethod = sigletInstance.getClass().getMethod("getClassToCheck");
        assertNotEquals(CommandLine.class, getClassToCheckMethod.invoke(sigletInstance));

    }

}