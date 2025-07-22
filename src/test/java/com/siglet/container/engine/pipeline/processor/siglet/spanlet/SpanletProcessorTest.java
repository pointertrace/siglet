package com.siglet.container.engine.pipeline.processor.siglet.spanlet;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.signal.trace.Span;
import com.siglet.api.signal.trace.Spanlet;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.eventloop.MapSignalDestination;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpanletProcessorTest {
    @Test
    void process() {

        SpanletTest modifiableSpanlet = new SpanletTest();

        SpanletConfig config = new SpanletConfig("prefix-");

        SpanletProcessor spanletEventLoop = new SpanletProcessor("prefix",
                modifiableSpanlet,config,5,1, Map.of());

        MapSignalDestination finalDestination = new MapSignalDestination("final");

        spanletEventLoop.connect(finalDestination);

        spanletEventLoop.start();

        io.opentelemetry.proto.trace.v1.Span span = io.opentelemetry.proto.trace.v1.Span.newBuilder()
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

    public static class SpanletTest implements Spanlet<SpanletConfig> {

        @Override
        public Result span(Span span, ProcessorContext<SpanletConfig> processorContext,
                           ResultFactory resultFactory) {
            processorContext.setAttribute("name-with-prefix",processorContext.getConfig().prefix + span.getName());
            span.setName(processorContext.getConfig().prefix + span.getName());
            return resultFactory.proceed();
        }
    }

}