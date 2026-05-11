package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action.GroovyActionConfig;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter.GroovyFilterConfig;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PipelineDescriptorTest {

    private ProcessorTypeRegistry processorTypeRegistry;

    @BeforeEach
    void setUp() {
        processorTypeRegistry = new ProcessorTypeRegistry();
    }

    @Test
    void parsePipelineWithSingleProcessor() {
        var config = """
                name: my-pipeline
                from: receiver-name
                start: action-processor
                processors:
                  - spanlet-groovy-action: action-processor
                    config:
                      action: println 'Processing'
                    to: filter-processor
                  - spanlet-groovy-filter: filter-processor
                    config:
                      expression: result.statusCode == 200
                """;

        Schema schema = PipelineDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        PipelineDescriptor pipelineDescriptor = factory.create(PipelineDescriptor.class);

        assertEquals("my-pipeline", pipelineDescriptor.getName().getValue());
        assertEquals(Location.of(1, 7), pipelineDescriptor.getName().getLocation());
        assertEquals("receiver-name", pipelineDescriptor.getFrom().getValue());
        assertEquals(Location.of(2, 7), pipelineDescriptor.getFrom().getLocation());
        assertEquals(1, pipelineDescriptor.getStart().size());
        assertEquals("action-processor", pipelineDescriptor.getStart().getFirst().getValue());
        assertEquals(Location.of(3, 8), pipelineDescriptor.getStart().getFirst().getLocation());
        assertEquals(2, pipelineDescriptor.getProcessors().size());

        ProcessorDescriptor firstProcessor = pipelineDescriptor.getProcessors().getFirst();
        assertEquals("spanlet-groovy-action", firstProcessor.getType().getValue());
        assertEquals("action-processor", firstProcessor.getName().getValue());

        GroovyActionConfig actionConfig = assertInstanceOf(GroovyActionConfig.class, firstProcessor.getConfig());
        assertEquals("println 'Processing'", actionConfig.getAction());

        ProcessorDescriptor secondProcessor = pipelineDescriptor.getProcessors().get(1);
        assertEquals("spanlet-groovy-filter", secondProcessor.getType().getValue());
        assertEquals("filter-processor", secondProcessor.getName().getValue());

        GroovyFilterConfig filterConfig = assertInstanceOf(GroovyFilterConfig.class, secondProcessor.getConfig());
        assertEquals("result.statusCode == 200", filterConfig.getExpression());
    }

    @Test
    void parsePipelineMinimal() {
        var config = """
                name: simple-pipeline
                from: receiver
                start: processor1
                processors:
                  - spanlet-groovy-action: processor1
                    config:
                      action: println 'test'
                """;

        Schema schema = PipelineDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        PipelineDescriptor pipelineDescriptor = factory.create(PipelineDescriptor.class);

        assertEquals("simple-pipeline", pipelineDescriptor.getName().getValue());
        assertEquals(Location.of(1, 7), pipelineDescriptor.getName().getLocation());
        assertEquals("receiver", pipelineDescriptor.getFrom().getValue());
        assertEquals(Location.of(2, 7), pipelineDescriptor.getFrom().getLocation());
        assertEquals(1, pipelineDescriptor.getStart().size());
        assertEquals("processor1", pipelineDescriptor.getStart().getFirst().getValue());
        assertEquals(Location.of(3, 8), pipelineDescriptor.getStart().getFirst().getLocation());
        assertEquals(1, pipelineDescriptor.getProcessors().size());
    }

    @Test
    void parsePipelineWithMultipleStartProcessors() {
        var config = """
                name: multi-start-pipeline
                from: receiver
                start:
                  - processor1
                  - processor2
                  - processor3
                processors:
                  - spanlet-groovy-action: processor1
                    config:
                      action: println 'one'
                  - spanlet-groovy-action: processor2
                    config:
                      action: println 'two'
                  - spanlet-groovy-action: processor3
                    config:
                      action: println 'three'
                """;

        Schema schema = PipelineDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        PipelineDescriptor pipelineDescriptor = factory.create(PipelineDescriptor.class);

        assertEquals("multi-start-pipeline", pipelineDescriptor.getName().getValue());
        assertEquals(Location.of(1, 7), pipelineDescriptor.getName().getLocation());
        assertEquals("receiver", pipelineDescriptor.getFrom().getValue());
        assertEquals(Location.of(2, 7), pipelineDescriptor.getFrom().getLocation());
        assertEquals(3, pipelineDescriptor.getStart().size());
        assertEquals("processor1", pipelineDescriptor.getStart().get(0).getValue());
        assertEquals(Location.of(4, 5), pipelineDescriptor.getStart().get(0).getLocation());
        assertEquals("processor2", pipelineDescriptor.getStart().get(1).getValue());
        assertEquals(Location.of(5, 5), pipelineDescriptor.getStart().get(1).getLocation());
        assertEquals("processor3", pipelineDescriptor.getStart().get(2).getValue());
        assertEquals(Location.of(6, 5), pipelineDescriptor.getStart().get(2).getLocation());
        assertEquals(3, pipelineDescriptor.getProcessors().size());
    }

    @Test
    void parsePipelineWithProcessorChain() {
        var config = """
                name: chained-pipeline
                from: receiver
                start:
                  - action-processor
                processors:
                  - spanlet-groovy-action: action-processor
                    config:
                      action: println 'first'
                    to:
                      - filter-processor
                  - spanlet-groovy-filter: filter-processor
                    config:
                      expression: result.code == 'OK'
                    to:
                      - final-processor
                  - spanlet-groovy-action: final-processor
                    config:
                      action: println 'done'
                """;

        Schema schema = PipelineDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        PipelineDescriptor pipelineDescriptor = factory.create(PipelineDescriptor.class);

        assertEquals("chained-pipeline", pipelineDescriptor.getName().getValue());
        assertEquals(Location.of(1, 7), pipelineDescriptor.getName().getLocation());
        assertEquals("receiver", pipelineDescriptor.getFrom().getValue());
        assertEquals(Location.of(2, 7), pipelineDescriptor.getFrom().getLocation());
        assertEquals(1, pipelineDescriptor.getStart().size());
        assertEquals("action-processor", pipelineDescriptor.getStart().getFirst().getValue());
        assertEquals(Location.of(4, 5), pipelineDescriptor.getStart().getFirst().getLocation());
        assertEquals(3, pipelineDescriptor.getProcessors().size());

        // Check processor chain
        assertEquals("action-processor", pipelineDescriptor.getProcessors().getFirst().getName().getValue());
        assertEquals(1, pipelineDescriptor.getProcessors().getFirst().getTo().size());
        assertEquals("filter-processor", pipelineDescriptor.getProcessors().getFirst().getTo().getFirst().getValue());
        assertEquals(Location.of(10, 9), pipelineDescriptor.getProcessors().getFirst().getTo().getFirst().getLocation());

        assertEquals("filter-processor", pipelineDescriptor.getProcessors().get(1).getName().getValue());
        assertEquals(1, pipelineDescriptor.getProcessors().get(1).getTo().size());
        assertEquals("final-processor", pipelineDescriptor.getProcessors().get(1).getTo().getFirst().getValue());
        assertEquals(Location.of(15, 9), pipelineDescriptor.getProcessors().get(1).getTo().getFirst().getLocation());

        assertEquals("final-processor", pipelineDescriptor.getProcessors().get(2).getName().getValue());
        assertEquals(0, pipelineDescriptor.getProcessors().get(2).getTo().size());
    }

    @Test
    void parsePipelineWithLocationInformation() {
        var config = """
                name: location-test
                from: receiver
                start:
                  - processor
                processors:
                  - spanlet-groovy-action: processor
                    config:
                      action: println 'test'
                """;

        Schema schema = PipelineDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        PipelineDescriptor pipelineDescriptor = factory.create(PipelineDescriptor.class);

        assertEquals(Location.of(1, 1), pipelineDescriptor.getLocation());
        assertEquals("location-test", pipelineDescriptor.getName().getValue());
        assertEquals(Location.of(1, 7), pipelineDescriptor.getName().getLocation());
    }

    @Test
    void parsePipelineWithEmptyStartList() {
        var config = """
                name: pipeline
                from: receiver
                start:
                processors:
                  - spanlet-groovy-action: processor
                    config:
                      action: println 'test'
                """;

        Schema schema = PipelineDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        PipelineDescriptor pipelineDescriptor = factory.create(PipelineDescriptor.class);

        assertEquals("pipeline", pipelineDescriptor.getName().getValue());
        assertEquals(Location.of(1, 7), pipelineDescriptor.getName().getLocation());
        assertEquals("receiver", pipelineDescriptor.getFrom().getValue());
        assertEquals(Location.of(2, 7), pipelineDescriptor.getFrom().getLocation());
        assertEquals(0, pipelineDescriptor.getStart().size());
        assertEquals(1, pipelineDescriptor.getProcessors().size());
    }

    @Test
    void validateAutoReferenceStart() {

        var config = """
                name: pipeline
                from: receiver
                start: pipeline
                processors:
                  - spanlet-groovy-action: processor
                    config:
                      action: println 'test'
                """;

        Schema schema = PipelineDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        PipelineDescriptor pipelineDescriptor = factory.create(PipelineDescriptor.class);
        SigletError e = assertThrows(SigletError.class, pipelineDescriptor::validate);
        assertEquals("Pipeline [pipeline] at (1,1) has an auto reference.", e.getMessage());
    }

    @Test
    void validateAutoReferenceFrom() {

        var config = """
                name: pipeline
                from: pipeline
                start: processor
                processors:
                  - spanlet-groovy-action: processor
                    config:
                      action: println 'test'
                """;

        Schema schema = PipelineDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        PipelineDescriptor pipelineDescriptor = factory.create(PipelineDescriptor.class);
        SigletError e = assertThrows(SigletError.class, pipelineDescriptor::validate);
        assertEquals("Pipeline [pipeline] at (1,1) has an auto reference.", e.getMessage());
    }
}