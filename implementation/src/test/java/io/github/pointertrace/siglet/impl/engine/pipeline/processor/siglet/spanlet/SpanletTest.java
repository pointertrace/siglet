package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorConfigCreationUtils;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;
import static org.junit.jupiter.api.Assertions.*;

class SpanletTest {

    private Span actualDefaultSignal;

    private String actualDefaultDestination;

    private Span actualOtherSignal;

    private String actualOtherDestination;

    private SpanAdapter spanAdapter;

    private final SignalEmitterFunction emitterFunction = (signal, destination) -> {

        if (SignalDestination.isAll(destination) || SignalDestination.isDrop(destination) || destination.equals("default")) {
            actualDefaultSignal = (Span) signal;
            actualDefaultDestination = destination;
        } else if (destination.equals("other")) {
            actualOtherSignal = (Span) signal;
            actualOtherDestination = destination;
        } else {
            throw new SigletError("Unknown destination: " + destination);
        }


    };


    @BeforeEach
    public void setUp() {

        actualDefaultSignal = null;

        actualDefaultDestination = null;

        actualOtherSignal = null;

        actualOtherDestination = null;

        io.opentelemetry.proto.trace.v1.Span span = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setName("span-name")
                .setSpanId(AdapterUtils.spanId(1))
                .setTraceId(AdapterUtils.traceId(0, 1))
                .build();

        Resource resource = Resource.newBuilder().build();

        InstrumentationScope scope = InstrumentationScope.newBuilder().setName("scope").build();

        spanAdapter = new SpanAdapter(span, resource, scope);

    }

    @Test
    public void process() {

        String config = """
                prefix-spanlet: spanlet
                config:
                  prefix: prefix-
                to: default
                """;

        ProcessorDescriptor processorDescriptor = ProcessorConfigCreationUtils
                .createProcessorDescriptor(config, "prefix-spanlet", new PrefixSpanlet(), configurationFactoryCreator());

        ProcessorNode processorNode = ProcessorConfigCreationUtils.createProcessorNode(processorDescriptor);

        SigletContext sigletContext = ProcessorConfigCreationUtils.creteSigletContext(processorDescriptor);

        SpanletProcessor spanletProcessor = new SpanletProcessor(sigletContext, processorNode,
                SpanletProcessorType.createSpanletFunction(new PrefixSpanlet()));

        spanletProcessor.setSignalEmitterFunction(emitterFunction);

        spanletProcessor.start();

        spanletProcessor.receive(spanAdapter);

        spanletProcessor.stop();


        assertNull(actualOtherDestination);
        assertNull(actualOtherSignal);

        assertEquals(SignalDestination.ALL, actualDefaultDestination);
        assertEquals("prefix-span-name", actualDefaultSignal.getName());
        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    public void process_drop() {

        String config = """
                prefix-spanlet-drop: spanlet
                config:
                  prefix: prefix-
                to: default
                """;

        ProcessorDescriptor processorDescriptor = ProcessorConfigCreationUtils
                .createProcessorDescriptor(config, "prefix-spanlet-drop", new PrefixSpanletDrop(), configurationFactoryCreator());

        ProcessorNode processorNode = ProcessorConfigCreationUtils.createProcessorNode(processorDescriptor);

        SigletContext sigletContext = ProcessorConfigCreationUtils.creteSigletContext(processorDescriptor);

        SpanletProcessor spanletProcessor = new SpanletProcessor(sigletContext, processorNode,
                SpanletProcessorType.createSpanletFunction(new PrefixSpanletDrop()));

        spanletProcessor.setSignalEmitterFunction(emitterFunction);

        spanletProcessor.start();

        spanletProcessor.receive(spanAdapter);

        spanletProcessor.stop();

        assertNull(actualOtherDestination);
        assertNull(actualOtherSignal);

        assertEquals(SignalDestination.DROP, actualDefaultDestination);
        assertEquals("prefix-span-name", actualDefaultSignal.getName());
        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    public void process_proceedToDestination() {

        String config = """
                prefix-spanlet-destination: spanlet
                config:
                  prefix: prefix-
                to:
                  - default
                  - other
                """;

        ProcessorDescriptor processorDescriptor = ProcessorConfigCreationUtils
                .createProcessorDescriptor(config, "prefix-spanlet-destination",
                        new PrefixSpanletProceedToDestination(), configurationFactoryCreator());

        ProcessorNode processorNode = ProcessorConfigCreationUtils.createProcessorNode(processorDescriptor);

        SigletContext sigletContext = ProcessorConfigCreationUtils.creteSigletContext(processorDescriptor);

        SpanletProcessor spanletProcessor = new SpanletProcessor(sigletContext, processorNode,
                SpanletProcessorType.createSpanletFunction(new PrefixSpanletProceedToDestination()));


        spanletProcessor.setSignalEmitterFunction(emitterFunction);

        spanletProcessor.start();

        spanletProcessor.receive(spanAdapter);

        spanletProcessor.stop();

        assertNull(actualDefaultDestination);
        assertNull(actualDefaultSignal);

        assertEquals("other", actualOtherDestination);
        assertEquals("prefix-span-name", actualOtherSignal.getName());
        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));


    }

    @Test
    public void process_proceedToDestinationMapping() {

        String config = """
                prefix-spanlet-destination-mapped: spanlet
                config:
                  prefix: prefix-
                to:
                  - default
                  - other-mapping:other
                """;

        ProcessorDescriptor processorDescriptor = ProcessorConfigCreationUtils
                .createProcessorDescriptor(config, "prefix-spanlet-destination-mapped",
                        new PrefixSpanletProceedToDestinationMapped(), configurationFactoryCreator());

        ProcessorNode processorNode = ProcessorConfigCreationUtils.createProcessorNode(processorDescriptor);

        SigletContext sigletContext = ProcessorConfigCreationUtils.creteSigletContext(processorDescriptor);

        SpanletProcessor spanletProcessor = new SpanletProcessor(sigletContext, processorNode,
                SpanletProcessorType.createSpanletFunction(new PrefixSpanletProceedToDestinationMapped()));


        assertNull(actualDefaultDestination);
        assertNull(actualDefaultSignal);

        spanletProcessor.setSignalEmitterFunction(emitterFunction);

        spanletProcessor.start();

        spanletProcessor.receive(spanAdapter);

        spanletProcessor.stop();

        assertEquals("other", actualOtherDestination);
        assertEquals("prefix-span-name", actualOtherSignal.getName());
        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));

        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    public static class PrefixSpanlet implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.proceed();
        }
    }

    public static class PrefixSpanletDrop implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.drop();
        }
    }

    public static class PrefixSpanletProceedToDestination implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.proceed("other");
        }
    }

    public static class PrefixSpanletProceedToDestinationMapped implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.proceed("other-mapping");
        }
    }

    public static class Config {

        private String prefix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

    public ConfigurationFactory<?> configurationFactoryCreator() {
        return ConfigurationFactory.of(
                List.of(property("prefix", Config::setPrefix, string())), Config.class);
    }


}