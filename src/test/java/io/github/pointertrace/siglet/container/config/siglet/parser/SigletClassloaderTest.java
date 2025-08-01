package io.github.pointertrace.siglet.container.config.siglet.parser;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siget.example.simplesuffixspanlet.siglet.SuffixSpanlet;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultImpl;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SigletClassloaderTest {

    private SigletClassLoader sigletClassLoader;


    @BeforeEach
    void setUp() throws Exception {

        File simpleSpanletJar = new File(SuffixSpanlet.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        sigletClassLoader = new SigletClassLoader(simpleSpanletJar, SigletClassLoader.class.getClassLoader());
    }

    @Test
    void getSigletConfig() throws IOException {

        String expectedSigletConfig = """
                siglets:
                  - name: suffix-spanlet
                    siglet-class: io.github.pointertrace.siget.example.simplesuffixspanlet.siglet.SuffixSpanlet
                    checker-factory-class: io.github.pointertrace.siget.example.simplesuffixspanlet.parser.SuffixConfigChecker
                    description: adds a span name suffix""";

        InputStream sigletConfigIS = sigletClassLoader.getSigletConfigInputStream();

        assertNotNull(sigletConfigIS);

        assertEquals(expectedSigletConfig,new String(sigletConfigIS.readAllBytes(), StandardCharsets.UTF_8));
    }

    @Test
    void getSigletConfig_nonExistingConfigFile() throws Exception {

        File simpleSpanletJar =
                new File(CommandLine.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        sigletClassLoader = new SigletClassLoader(simpleSpanletJar, SigletClassLoader.class.getClassLoader());

        SigletError e = assertThrows(SigletError.class, sigletClassLoader::getSigletConfigInputStream);

        assertTrue(e.getMessage().startsWith("Could not find META-INF/siglet-config.yaml in the jar file"));

        assertTrue(e.getMessage().endsWith(".jar"));


    }


    @Test
    void getSigletClass() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {


        Class<?> sigletClass = sigletClassLoader.loadClass(SuffixSpanlet.class.getName());


        assertNotNull(sigletClass);

        Spanlet<?> spanlet = (Spanlet<?>) sigletClass.getDeclaredConstructor().newInstance();

        Span span = Span.newBuilder()
                .setName("span-name")
                .build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter();
        protoSpanAdapter.recycle(span,null,null);

        assertEquals(ResultImpl.proceed(), spanlet.span(protoSpanAdapter, null, ResultFactoryImpl.INSTANCE));

    }

    @Test
    void getUrlRootJarFile() {

        URL url = sigletClassLoader.findResource("any-file.txt");

        assertNotNull(url);

    }

    @Test
    void getUrlNestedJarFile() {

        URL url = sigletClassLoader.findResource("LICENSE.txt");

        assertNotNull(url);

    }

    @Test
    void getContentRootJarFile() throws IOException {


        InputStream is = sigletClassLoader.getResourceAsStream("any-file.txt");

        assertNotNull(is);

        assertEquals("a content inside a resources file", new String(is.readAllBytes(),StandardCharsets.UTF_8));

    }

    @Test
    void getContentNestedJarFile() throws IOException {


        InputStream is = sigletClassLoader.getResourceAsStream("LICENSE.txt");

        assertNotNull(is);

        assertTrue(new String(is.readAllBytes(),StandardCharsets.UTF_8).startsWith("BSD License"));

    }

    @Test
    void checkClassFromSpanletAndFromCurrentClassLoaderAreDifferent() throws Exception {

        Class<?> sigletClass = sigletClassLoader.loadClass(SuffixSpanlet.class.getName());


        assertNotNull(sigletClass);

        Spanlet<?> spanlet = (Spanlet<?>) sigletClass.getDeclaredConstructor().newInstance();

        Class<?> commandLineFromSigletJarClass = (Class<?>) spanlet.getClass().getMethod("getClassToCheck").invoke(spanlet);

        assertNotSame(CommandLine.class, commandLineFromSigletJarClass);


    }

}