package com.siglet.container.engine.pipeline.processor.siglet.spanlet.modifiable;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;
import com.siglet.api.modifiable.trace.ModifiableSpan;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.eventloop.MapSignalDestination;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModifiableSpanletProcessorTest {
    @Test
    void process() {

        ModifiableSpanletTest modifiableSpanlet = new ModifiableSpanletTest();

        SpanletConfig config = new SpanletConfig("prefix-");

        ModifiableSpanletProcessor spanletEventLoop = new ModifiableSpanletProcessor("prefix",
                modifiableSpanlet,config,5,1, Map.of());

        MapSignalDestination finalDestination = new MapSignalDestination("final", Signal.class);

        spanletEventLoop.connect(finalDestination);

        spanletEventLoop.start();

        Span span = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0,1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("span-name")
                .build();

        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, null, null);


        assertTrue(spanletEventLoop.send(protoSpanAdapter));

        spanletEventLoop.stop();

        assertEquals("prefix-span-name",spanletEventLoop.getContext().getAttribute("name-with-prefix",String.class));
        assertEquals("prefix-span-name",protoSpanAdapter.getName());

    }

    public static class SpanletConfig {
        private final String prefix;

        public SpanletConfig(String prefix) {
            this.prefix = prefix;
        }
    }

    public static class ModifiableSpanletTest implements  com.siglet.api.modifiable.trace.ModifiableSpanlet<SpanletConfig> {

        @Override
        public Result span(ModifiableSpan span, ProcessorContext<SpanletConfig> processorContext,
                           ResultFactory resultFactory) {
            processorContext.setAttribute("name-with-prefix",processorContext.getConfig().prefix + span.getName());
            span.setName(processorContext.getConfig().prefix + span.getName());
            return resultFactory.proceed();
        }
    }

}