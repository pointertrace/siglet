package io.github.pointertrace.siglet.container.config.siglet.springboot;

import io.github.pointertrace.siglet.container.config.siglet.ExampleJarsInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpringBootStartClassReaderTest {

    @Test
    void read() {

        assertEquals("io.github.pointertrace.siglet.example.springboot.suffix.SuffixSpanletApplication",
                SpringBootStartClassReader.read(ExampleJarsInfo.getSpringBootExampleSigletFile()));

    }
}