package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorConfigCreationUtils;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.GroovyProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.action.SpanletGroovyActionProcessorType;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpanletGroovyActionProcessorTest {

    private Span actualDefaultSignal;

    private String actualDefaultDestination;

    private Span actualOtherSignal;

    private String actualOtherDestination;

    private SpanAdapter spanAdapter;

    private SpanletGroovyActionProcessorType processorType;

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

        processorType = new SpanletGroovyActionProcessorType();
    }

    @Test
    void process() {

        String config = """
                spanlet-groovy-action: action
                config:
                  action: |
                    signal.name = "prefix-" + signal.name
                    context.attributes["new-name"] = signal.name
                to: default
                """;

        ProcessorDescriptor processorDescriptor = ProcessorConfigCreationUtils.createProcessorDescriptor(config);

        ProcessorNode processorNode = ProcessorConfigCreationUtils.createProcessorNode(processorDescriptor);

        SigletContext sigletContext = ProcessorConfigCreationUtils.creteSigletContext(processorDescriptor);

        GroovyProcessor groovyProcessor = assertInstanceOf(GroovyProcessor.class,
                processorType.getComponentCreator().create(sigletContext, processorNode));

        groovyProcessor.setSignalEmitterFunction(emitterFunction);

        groovyProcessor.start();

        assertTrue(groovyProcessor.receive(spanAdapter));

        groovyProcessor.stop();

        assertEquals(SignalDestination.ALL, actualDefaultDestination);
        assertEquals("prefix-span-name", actualDefaultSignal.getName());
        assertTrue(groovyProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", groovyProcessor.getContext().getAttributes().get("new-name"));

        assertNull(actualOtherDestination);
        assertNull(actualOtherSignal);
    }

    @Test
    void process_drop() {

        String config = """
                spanlet-groovy-action: action
                config:
                  action: |
                    signal.name = "prefix-" + signal.name
                    context.attributes["new-name"] = signal.name
                    drop()
                to: default
                """;

        ProcessorDescriptor processorDescriptor = ProcessorConfigCreationUtils.createProcessorDescriptor(config);

        ProcessorNode processorNode = ProcessorConfigCreationUtils.createProcessorNode(processorDescriptor);

        SigletContext sigletContext = ProcessorConfigCreationUtils.creteSigletContext(processorDescriptor);

        GroovyProcessor groovyProcessor = assertInstanceOf(GroovyProcessor.class,
                processorType.getComponentCreator().create(sigletContext, processorNode));


        groovyProcessor.setSignalEmitterFunction(emitterFunction);

        groovyProcessor.start();

        groovyProcessor.receive(spanAdapter);

        groovyProcessor.stop();


        assertEquals(SignalDestination.DROP, actualDefaultDestination);
        assertEquals("prefix-span-name", actualDefaultSignal.getName());
        assertTrue(groovyProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", groovyProcessor.getContext().getAttributes().get("new-name"));

        assertNull(actualOtherDestination);
        assertNull(actualOtherSignal);
    }

    @Test
    void process_proceedToDestination() {

        String config = """
                spanlet-groovy-action: action
                config:
                  action: |
                    signal.name = "prefix-" + signal.name
                    context.attributes["new-name"] = signal.name
                    proceed("other")
                to:
                  - default
                  - other
                """;

        ProcessorDescriptor processorDescriptor = ProcessorConfigCreationUtils.createProcessorDescriptor(config);

        ProcessorNode processorNode = ProcessorConfigCreationUtils.createProcessorNode(processorDescriptor);

        SigletContext sigletContext = ProcessorConfigCreationUtils.creteSigletContext(processorDescriptor);

        GroovyProcessor groovyProcessor = assertInstanceOf(GroovyProcessor.class,
                processorType.getComponentCreator().create(sigletContext, processorNode));


        groovyProcessor.setSignalEmitterFunction(emitterFunction);

        groovyProcessor.start();

        groovyProcessor.receive(spanAdapter);

        groovyProcessor.stop();

        assertNull(actualDefaultDestination);
        assertNull(actualDefaultSignal);

        assertEquals("other", actualOtherDestination);
        assertEquals("prefix-span-name", actualOtherSignal.getName());
        assertTrue(groovyProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", groovyProcessor.getContext().getAttributes().get("new-name"));

    }

}