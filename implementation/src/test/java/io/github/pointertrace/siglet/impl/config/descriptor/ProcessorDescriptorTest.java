package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.action.GroovyActionConfig;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.filter.GroovyFilterConfig;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.router.GroovyRouterConfig;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorDescriptorTest {

    private ProcessorTypeRegistry processorTypeRegistry;

    @BeforeEach
    void setUp() {
        processorTypeRegistry = new ProcessorTypeRegistry();
    }

    @Test
    void parseSpanletGroovyActionProcessor() {
        var config = """
                spanlet-groovy-action: processor-name
                config:
                  action: println 'Action'
                to: next-processor
                """;

        Schema schema = ProcessorDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ProcessorDescriptor processorDescriptor = factory.create(ProcessorDescriptor.class);

        assertEquals("spanlet-groovy-action", processorDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), processorDescriptor.getType().getLocation());
        assertEquals("processor-name", processorDescriptor.getName().getValue());
        assertEquals(Location.of(1, 24), processorDescriptor.getName().getLocation());

        GroovyActionConfig actionConfig = assertInstanceOf(GroovyActionConfig.class, processorDescriptor.getConfig());
        assertEquals("println 'Action'", actionConfig.getAction());

        assertEquals(1, processorDescriptor.getTo().size());
        assertEquals("next-processor", processorDescriptor.getTo().getFirst().getValue());
        assertEquals(Location.of(4, 5), processorDescriptor.getTo().getFirst().getLocation());
    }

    @Test
    void parseSpanletGroovyActionProcessorMinimal() {
        var config = """
                spanlet-groovy-action: processor-name
                config:
                  action: println 'Action'
                """;

        Schema schema = ProcessorDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ProcessorDescriptor processorDescriptor = factory.create(ProcessorDescriptor.class);

        assertEquals("spanlet-groovy-action", processorDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), processorDescriptor.getType().getLocation());
        assertEquals("processor-name", processorDescriptor.getName().getValue());
        assertEquals(Location.of(1, 24), processorDescriptor.getName().getLocation());

        GroovyActionConfig actionConfig = assertInstanceOf(GroovyActionConfig.class, processorDescriptor.getConfig());
        assertEquals("println 'Action'", actionConfig.getAction());

        assertEquals(0, processorDescriptor.getTo().size());
        assertNull(processorDescriptor.getQueueSize());
        assertNull(processorDescriptor.getThreadPoolSize());
    }

    @Test
    void parseSpanletGroovyFilterProcessor() {
        var config = """
                spanlet-groovy-filter: filter-processor
                queue-size: 1024
                thread-pool-size: 4
                config:
                  expression: result.statusCode == 200
                to: success-processor
                """;

        Schema schema = ProcessorDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ProcessorDescriptor processorDescriptor = factory.create(ProcessorDescriptor.class);

        assertEquals("spanlet-groovy-filter", processorDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), processorDescriptor.getType().getLocation());
        assertEquals("filter-processor", processorDescriptor.getName().getValue());
        assertEquals(Location.of(1, 24), processorDescriptor.getName().getLocation());

        GroovyFilterConfig filterConfig = assertInstanceOf(GroovyFilterConfig.class, processorDescriptor.getConfig());
        assertEquals("result.statusCode == 200", filterConfig.getExpression());

        assertEquals(1, processorDescriptor.getTo().size());
        assertEquals("success-processor", processorDescriptor.getTo().getFirst().getValue());
        assertEquals(Location.of(6, 5), processorDescriptor.getTo().getFirst().getLocation());
        assertEquals(1024, processorDescriptor.getQueueSize().getValue().intValue());
        assertEquals(Location.of(2, 13), processorDescriptor.getQueueSize().getLocation());
        assertEquals(4, processorDescriptor.getThreadPoolSize().getValue().intValue());
        assertEquals(Location.of(3, 19), processorDescriptor.getThreadPoolSize().getLocation());
    }

    @Test
    void parseSpanletGroovyFilterProcessorMinimal() {
        var config = """
                spanlet-groovy-filter: filter-processor
                config:
                  expression: result.statusCode == 200
                """;

        Schema schema = ProcessorDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ProcessorDescriptor processorDescriptor = factory.create(ProcessorDescriptor.class);

        assertEquals("spanlet-groovy-filter", processorDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), processorDescriptor.getType().getLocation());
        assertEquals("filter-processor", processorDescriptor.getName().getValue());
        assertEquals(Location.of(1, 24), processorDescriptor.getName().getLocation());

        GroovyFilterConfig filterConfig = assertInstanceOf(GroovyFilterConfig.class, processorDescriptor.getConfig());
        assertEquals("result.statusCode == 200", filterConfig.getExpression());

        assertEquals(0, processorDescriptor.getTo().size());
    }

    @Test
    void parseSpanletGroovyRouterProcessor() {
        var config = """
                spanlet-groovy-router: router-processor
                config:
                  default: default-route
                  routes:
                    - to: route-one
                      when: context.status == 'success'
                    - to: route-two
                      when: context.status == 'error'
                to:
                  - route-one
                  - route-two
                """;

        Schema schema = ProcessorDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ProcessorDescriptor processorDescriptor = factory.create(ProcessorDescriptor.class);

        assertEquals("spanlet-groovy-router", processorDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), processorDescriptor.getType().getLocation());
        assertEquals("router-processor", processorDescriptor.getName().getValue());
        assertEquals(Location.of(1, 24), processorDescriptor.getName().getLocation());

        GroovyRouterConfig routerConfig = assertInstanceOf(GroovyRouterConfig.class, processorDescriptor.getConfig());
        assertEquals("default-route", routerConfig.getDefaultRoute().getValue());
        assertEquals(2, routerConfig.getRoutes().size());
        assertEquals("route-one", routerConfig.getRoutes().get(0).getTo().getValue());
        assertEquals("context.status == 'success'", routerConfig.getRoutes().get(0).getWhen().getValue());
        assertEquals("route-two", routerConfig.getRoutes().get(1).getTo().getValue());
        assertEquals("context.status == 'error'", routerConfig.getRoutes().get(1).getWhen().getValue());

        assertEquals(2, processorDescriptor.getTo().size());
        assertEquals("route-one", processorDescriptor.getTo().getFirst().getValue());
        assertEquals(Location.of(10, 5), processorDescriptor.getTo().getFirst().getLocation());
        assertEquals("route-two", processorDescriptor.getTo().get(1).getValue());
        assertEquals(Location.of(11, 5), processorDescriptor.getTo().get(1).getLocation());
    }

    @Test
    void parseSpanletGroovyRouterProcessorMinimal() {
        var config = """
                spanlet-groovy-router: router-processor
                config:
                  default: default-route
                  routes:
                    - to: route-one
                      when: context.status == 'success'
                to:
                  - default-route
                  - route-one
                """;

        Schema schema = ProcessorDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ProcessorDescriptor processorDescriptor = factory.create(ProcessorDescriptor.class);

        assertEquals("spanlet-groovy-router", processorDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), processorDescriptor.getType().getLocation());
        assertEquals("router-processor", processorDescriptor.getName().getValue());
        assertEquals(Location.of(1, 24), processorDescriptor.getName().getLocation());

        GroovyRouterConfig routerConfig = assertInstanceOf(GroovyRouterConfig.class, processorDescriptor.getConfig());
        assertEquals("default-route", routerConfig.getDefaultRoute().getValue());
        assertEquals(1, routerConfig.getRoutes().size());

        assertEquals(2, processorDescriptor.getTo().size());
        assertEquals("default-route", processorDescriptor.getTo().getFirst().getValue());
        assertEquals(Location.of(8, 5), processorDescriptor.getTo().getFirst().getLocation());
        assertEquals("route-one", processorDescriptor.getTo().get(1).getValue());
        assertEquals(Location.of(9, 5), processorDescriptor.getTo().get(1).getLocation());
    }

    @Test
   void validateAutoReference() {

        var config = """
                spanlet-groovy-action: processor
                config:
                  action: println 'Action'
                to: processor
                """;

        Schema schema = ProcessorDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ProcessorDescriptor processorDescriptor = factory.create(ProcessorDescriptor.class);
        SigletError e = assertThrows(SigletError.class, processorDescriptor::validate);
        assertEquals("Processor [processor] at (1,1) has an auto reference.", e.getMessage());
    }

}