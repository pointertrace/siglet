package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.*;

class SpanletContextClassloaderSetterTest {

    @Test
    void addContextClassloaderSetter() {
        ClassLoader classLoader = new URLClassLoader(
                new URL[]{CommandLine.class.getProtectionDomain().getCodeSource().getLocation()});
        Spanlet<?> mockSpanlet = SpanletContextClassloaderSetter.addContextClassloaderSetter(new MockSpanlet(),
                classLoader);
        mockSpanlet.span(null, null, null);
        SpanletContextClassloaderSetter spanletContextClassloaderSetter =
                ((SpanletContextClassloaderSetter) Proxy.getInvocationHandler(mockSpanlet));

        assertSame(classLoader, ((MockSpanlet) spanletContextClassloaderSetter.getSpanlet()).contextClassloader);
    }

    private static class MockSpanlet implements Spanlet<Object> {

        public ClassLoader contextClassloader;

        @Override
        public Result span(Span span, ProcessorContext<Object> processorContext, ResultFactory resultFactory) {
            contextClassloader = Thread.currentThread().getContextClassLoader();
            return null;
        }
    }

}