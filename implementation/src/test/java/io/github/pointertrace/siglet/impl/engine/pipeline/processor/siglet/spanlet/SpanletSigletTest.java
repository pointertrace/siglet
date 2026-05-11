package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpanletSigletTest {

    @Test
    void process() {

        SpanletTest spanlet = new SpanletTest();

        SpanletConfig config = new SpanletConfig("prefix-");

        SpanletProcessor spanletEventLoop = new SpanletProcessor("prefix", spanlet,config,5,1, Map.of());

        MockSignalDestination finalDestination = new MockSignalDestination("final",
                SignalCapabilities.of(Span.class));

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

        assertEquals("prefix-span-name",spanletEventLoop.getContext().getAttributes() .get("name-with-prefix"));
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
        public Result span(Span span, Context<SpanletConfig> context,
                           ResultFactory resultFactory) {
            context.getAttributes().put("name-with-prefix", context.getConfig().prefix + span.getName());
            span.setName(context.getConfig().prefix + span.getName());
            return resultFactory.proceed();
        }
    }

}