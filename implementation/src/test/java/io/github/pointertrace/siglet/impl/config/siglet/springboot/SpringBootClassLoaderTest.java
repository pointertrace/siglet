package io.github.pointertrace.siglet.impl.config.siglet.springboot;

import io.github.pointertrace.internallib.InternalLibClass;
import io.github.pointertrace.siglet.impl.config.siglet.ExampleJarsInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.loader.launch.Archive;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SpringBootClassLoaderTest {

    private Archive archive;

    private SpringBootClassLoader springBootClassLoader;


    @BeforeEach
    void setUp() throws Exception {

        archive = Archive.create(ExampleJarsInfo.getSpringBootExampleSigletFile());

        springBootClassLoader =
                SpringBootClassLoader.of(archive, SpringBootClassLoader.class.getClassLoader());


    }

    @AfterEach
    void tearDown() throws Exception {
        archive.close();
    }

    @Test
    void getUrlRootJarFile() {

        URL url = springBootClassLoader.findResource("any-file.txt");
        assertNotNull(url);

    }

    @Test
    void getUrlNestedJarFile() {

        URL url = springBootClassLoader.findResource("internal-lib-file.txt");

        assertNotNull(url);

    }

    @Test
    void getContentRootJarFile() throws IOException {


        InputStream is = springBootClassLoader.getResourceAsStream("any-file.txt");

        assertNotNull(is);

        assertEquals("a content inside a resources file", new String(is.readAllBytes(), StandardCharsets.UTF_8));

    }

    @Test
    void getContentNestedJarFile() throws IOException {


        InputStream is = springBootClassLoader.getResourceAsStream("internal-lib-file.txt");

        assertNotNull(is);

        assertTrue(new String(is.readAllBytes(), StandardCharsets.UTF_8).startsWith("Internal lib file content"));

    }

    @Test
    void checkClassFromSpanletAndFromCurrentClassLoaderAreDifferent() throws Exception {

        Class<?> internalLibClass = springBootClassLoader.loadClass(InternalLibClass.class.getName());

        assertNotNull(internalLibClass);


        assertNotSame(InternalLibClass.class, internalLibClass);


    }
}