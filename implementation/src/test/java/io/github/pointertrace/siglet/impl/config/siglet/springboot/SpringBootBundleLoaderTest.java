package io.github.pointertrace.siglet.impl.config.siglet.springboot;

import io.github.pointertrace.siglet.impl.config.siglet.ExampleJarsInfo;
import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpringBootBundleLoaderTest {

    private SpringBootBundleLoader springBootBundleLoader;


    @BeforeEach
    void setUp() {
        springBootBundleLoader = new SpringBootBundleLoader();
    }

    @Test
    void load() {

     SigletBundle sigletBundle = springBootBundleLoader.load(ExampleJarsInfo.getSpringBootExampleSigletFile());

     assertEquals(1, sigletBundle.getDefinitions().size());

    }

}