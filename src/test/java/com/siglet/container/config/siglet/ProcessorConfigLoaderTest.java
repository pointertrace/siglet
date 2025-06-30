package com.siglet.container.config.siglet;

import com.siglet.api.Processor;
import com.siglet.api.parser.NodeChecker;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.config.siglet.SigletConfigLoader;
import com.siglet.container.eventloop.processor.result.ResultFactoryImpl;
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
    public void setUp(){

        sigletConfigLoader = new SigletConfigLoader();

        spanletUrl= Thread.currentThread().getContextClassLoader().getResource("SimpleSpanlet-1.0-SNAPSHOT.jar");

    }

    @Test
    void load() throws Exception {

        SigletConfig sigletConfig = sigletConfigLoader.load(Paths.get(spanletUrl.toURI()));

        assertNotNull(sigletConfig);
        assertEquals("suffix-spanlet",sigletConfig.name());
        assertEquals("adds a span name suffix",sigletConfig.description());
        assertInstanceOf(Class.class,sigletConfig.sigletClass());
        assertTrue(Processor.class.isAssignableFrom(sigletConfig.sigletClass()));
        assertInstanceOf(NodeChecker.class,sigletConfig.configChecker());

        Object sigletInstance = sigletConfig.createSigletInstance();
        Method spanMethod = sigletConfig.getSigletMethod();

        Span protoSpan = Span.newBuilder().setName("span-name").build();

        ProtoSpanAdapter span = new ProtoSpanAdapter().recycle(protoSpan,null,null);

        spanMethod.invoke(sigletInstance, span,null, ResultFactoryImpl.INSTANCE);


        assertEquals("0:span-name",span.getName());

        Method getClassToCheckMethod = sigletInstance.getClass().getMethod("getClassToCheck");
        assertNotEquals(CommandLine.class, getClassToCheckMethod.invoke(sigletInstance));

    }

}