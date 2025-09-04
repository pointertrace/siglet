package io.github.pointertrace.siglet.container.config.siglet.fatjar;

import io.github.pointertrace.internallib.InternalLibClass;
import io.github.pointertrace.siglet.container.config.siglet.ExampleJarsInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.*;

class FatJarClassloaderTest {

    private FatJarClassLoader fatJarClassLoader;

    @BeforeEach
    void setUp() throws Exception {

        fatJarClassLoader =
                new FatJarClassLoader(new JarFile(ExampleJarsInfo.getFatJarExampleSigletFile()),
                FatJarClassloaderTest.class.getClassLoader());
    }

    @Test
    void getUrlRootJarFile() {

        URL url = fatJarClassLoader.findResource("any-file.txt");
        assertNotNull(url);

    }

    @Test
    void getUrlNestedJarFile() {

        URL url = fatJarClassLoader.findResource("internal-lib-file.txt");

        assertNotNull(url);

    }

    @Test
    void getContentRootJarFile() throws IOException {


        InputStream is = fatJarClassLoader.getResourceAsStream("any-file.txt");

        assertNotNull(is);

        assertEquals("a content inside a resources file", new String(is.readAllBytes(), StandardCharsets.UTF_8));

    }

    @Test
    void getContentNestedJarFile() throws IOException {


        InputStream is = fatJarClassLoader.getResourceAsStream("internal-lib-file.txt");

        assertNotNull(is);

        assertTrue(new String(is.readAllBytes(), StandardCharsets.UTF_8).startsWith("Internal lib file content"));

    }

    @Test
    void checkClassFromSpanletAndFromCurrentClassLoaderAreDifferent() throws Exception {

        Class<?> internalLibClass = fatJarClassLoader.loadClass(InternalLibClass.class.getName());

        assertNotNull(internalLibClass);


        assertNotSame(InternalLibClass.class, internalLibClass);


    }

}