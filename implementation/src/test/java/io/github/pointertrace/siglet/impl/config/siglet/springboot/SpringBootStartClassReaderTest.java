package io.github.pointertrace.siglet.impl.config.siglet.springboot;

import io.github.pointertrace.siglet.impl.config.siglet.ExampleJarsInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpringBootStartClassReaderTest {

    @Test
    void read() {

        assertEquals("io.github.pointertrace.siglet.impl.test.bundle.springboot.suffix.SuffixSpanletApplication",
                SpringBootStartClassReader.read(ExampleJarsInfo.getSpringBootExampleSigletFile()));

    }
}