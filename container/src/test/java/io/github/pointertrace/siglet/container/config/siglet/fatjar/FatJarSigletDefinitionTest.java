package io.github.pointertrace.siglet.container.config.siglet.fatjar;

import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.NodeCheckerFactory;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.schema.IntChecker;
import io.github.pointertrace.siglet.api.Processor;
import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.siglet.SpanletContextClassloaderSetter;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FatJarSigletDefinitionTest {

    private final Location location = Location.of(1, 1);

    private ClassLoader classloader;

    private SigletConfig sigletConfig;

    private FatJarSigletDefinition fatJarSigletDefinition;


    @Test
    void createProcessor() {
        sigletConfig = new SigletConfig("test-config", location, "teste config description",
                location, MockSpanlet.class.getName(), location, null, location,
                List.of(), location);

        classloader = getClass().getClassLoader();

        fatJarSigletDefinition = new FatJarSigletDefinition(classloader, sigletConfig);

        Processor processor = fatJarSigletDefinition.createProcessor();

        assertNotNull(processor);

        Spanlet<?> spanlet = assertInstanceOf(Spanlet.class, processor);

        spanlet.span(null, null, null);

        InvocationHandler invocationHandler = Proxy.getInvocationHandler(processor);

        assertNotNull(invocationHandler);

        SpanletContextClassloaderSetter spanletContextClassloaderSetter =
                assertInstanceOf(SpanletContextClassloaderSetter.class, invocationHandler);

        MockSpanlet mockSpanlet = assertInstanceOf(MockSpanlet.class, spanletContextClassloaderSetter.getSpanlet());

        assertSame(classloader, mockSpanlet.classLoader);

    }

    @Test
    void createProcessor_notSigletClass() {
        sigletConfig = new SigletConfig("test-config", location, "teste config description",
                location, Object.class.getName(), location, null,
                location, List.of(), location);

        classloader = getClass().getClassLoader();

        fatJarSigletDefinition = new FatJarSigletDefinition(classloader, sigletConfig);

        SigletError e = assertThrows(SigletError.class, () -> fatJarSigletDefinition.createProcessor());

        assertEquals("Class java.lang.Object does not implements io.github.pointertrace.siglet.api.signal." +
                     "trace.Spanlet", e.getMessage());

    }

    @Test
    void createProcessor_withoutNonArgsConstructor() {
        sigletConfig = new SigletConfig("test-config", location, "teste config description",
                location, MockSpanletWithoutNonArgsConstructor.class.getName(), location,
                null, location, List.of(), location);

        classloader = getClass().getClassLoader();

        fatJarSigletDefinition = new FatJarSigletDefinition(classloader, sigletConfig);

        SigletError e = assertThrows(SigletError.class, () -> fatJarSigletDefinition.createProcessor());

        assertEquals("Class io.github.pointertrace.siglet.container.config.siglet.fatjar." +
                     "FatJarSigletDefinitionTest$MockSpanletWithoutNonArgsConstructor does not have a non-args public " +
                     "constructor", e.getMessage());

    }

    @Test
    void createConfigChecker() {

        sigletConfig = new SigletConfig("test-config", location, "teste config description",
                location, null, location, MockConfigCheckFactory.class.getName(), location,
                List.of(), location);

        classloader = getClass().getClassLoader();

        fatJarSigletDefinition = new FatJarSigletDefinition(classloader, sigletConfig);

        NodeChecker nodeChecker = fatJarSigletDefinition.createConfigChecker();

        assertNotNull(nodeChecker);
        assertInstanceOf(IntChecker.class, nodeChecker);
    }

    @Test
    void createConfigChecker_notSigletClass() {
        sigletConfig = new SigletConfig("test-config", location, "teste config description",
                location, null, location, Object.class.getName(),
                location, List.of(), location);

        classloader = getClass().getClassLoader();

        fatJarSigletDefinition = new FatJarSigletDefinition(classloader, sigletConfig);

        SigletError e = assertThrows(SigletError.class, () -> fatJarSigletDefinition.createConfigChecker());

        assertEquals("Class java.lang.Object does not implements io.github.pointertrace.siglet.parser." +
                     "NodeCheckerFactory", e.getMessage());

    }

    @Test
    void createConfigChecker_withoutNonArgsConstructor() {
        sigletConfig = new SigletConfig("test-config", location, "teste config description",
                location, null, location,
                MockConfigCheckFactoryWithoutNonArgsConstructor.class.getName(), location, List.of(), location);

        classloader = getClass().getClassLoader();

        fatJarSigletDefinition = new FatJarSigletDefinition(classloader, sigletConfig);

        SigletError e = assertThrows(SigletError.class, () -> fatJarSigletDefinition.createConfigChecker());

        assertEquals("Class io.github.pointertrace.siglet.container.config.siglet.fatjar." +
                     "FatJarSigletDefinitionTest$MockConfigCheckFactoryWithoutNonArgsConstructor does not have a " +
                     "non-args public constructor", e.getMessage());

    }

    private static class MockConfigCheckFactory implements NodeCheckerFactory {

        public MockConfigCheckFactory() {
        }

        public NodeChecker create() {
            return new IntChecker();
        }

    }

    private static class MockConfigCheckFactoryWithoutNonArgsConstructor implements NodeCheckerFactory {

        public MockConfigCheckFactoryWithoutNonArgsConstructor(String arg) {
        }

        public NodeChecker create() {
            return new IntChecker();
        }

    }

    private static class MockSpanlet implements Spanlet<Object> {

        public ClassLoader classLoader;

        public MockSpanlet() {
        }

        @Override
        public Result span(Span span, ProcessorContext<Object> processorContext, ResultFactory resultFactory) {
            classLoader = Thread.currentThread().getContextClassLoader();
            return null;
        }
    }

    private static class MockSpanletWithoutNonArgsConstructor implements Spanlet<Object> {

        public ClassLoader classLoader;

        public MockSpanletWithoutNonArgsConstructor(String arg) {
        }

        @Override
        public Result span(Span span, ProcessorContext<Object> processorContext, ResultFactory resultFactory) {
            classLoader = Thread.currentThread().getContextClassLoader();
            return null;
        }
    }

}