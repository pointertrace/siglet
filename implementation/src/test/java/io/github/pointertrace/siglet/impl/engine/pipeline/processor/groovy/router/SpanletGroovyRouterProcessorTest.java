package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

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
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.router.SpanletGroovyRouterProcessorType;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpanletGroovyRouterProcessorTest {

    private Span actualDefaultSignal;

    private String actualDefaultDestination;

    private Span actualOtherSignal;

    private String actualOtherDestination;

    private SpanAdapter spanAdapter;

    private SpanletGroovyRouterProcessorType processorType;

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

        processorType = new SpanletGroovyRouterProcessorType();
    }

    @Test
    void process_match() {

        String config = """
                spanlet-groovy-router: router
                config:
                  default: default
                  routes:
                    - when: signal.name == 'span-name'
                      to: other
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
        assertEquals("span-name", actualOtherSignal.getName());
    }

    @Test
    void process_default() {

        String config = """
                spanlet-groovy-router: router
                config:
                  default: default
                  routes:
                    - when: signal.name == 'other-name'
                      to: route1
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

        assertEquals("default", actualDefaultDestination);
        assertEquals("span-name", actualDefaultSignal.getName());

        assertNull(actualOtherDestination);
        assertNull(actualOtherSignal);


    }

}
