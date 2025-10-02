package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorCheckerDiscriminatorTest {

    private YamlParser parser;

    private ProcessorCheckerDiscriminator processorCheckerDiscriminator;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();

        ProcessorTypeRegistry processorTypeRegistry = new ProcessorTypeRegistry();

        processorCheckerDiscriminator = new ProcessorCheckerDiscriminator(processorTypeRegistry);
    }


    @Test
    void check() {

        var config = """
                spanlet-groovy-action: spanlet name
                to: second exporter
                config:
                  action: signal.name = signal.name +"-suffix" """;

        Node node = parser.parse(config);

        NodeChecker configNodeChecker = processorCheckerDiscriminator.getChecker(node);

        assertNotNull(configNodeChecker);

    }

    @Test
    void check_invalidSiglet() {

        var config = """
                invalid-siglet: siglet name
                to: second exporter
                config:
                  action: signal.name = signal.name +"-suffix" """;

        Node node = parser.parse(config);

        SigletError e = assertThrows(SigletError.class, () -> processorCheckerDiscriminator.getChecker(node));

        assertEquals("Processor type invalid-siglet at (1,1) should be one of: spanlet-groovy-filter, " +
                     "spanlet-groovy-action, spanlet-groovy-router", e.getMessage());

    }
}