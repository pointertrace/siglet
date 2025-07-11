package com.siglet.container.engine.pipeline.processor.siglet.spanlet.unmodifiable;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpan;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.eventloop.MapSignalDestination;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnmodifiableSpanletProcessorTest {

    @Test
    void process() {

        UnmodifiableSpanletTest unmodifiableSpanlet = new UnmodifiableSpanletTest();


        SpanletConfig config = new SpanletConfig("prefix-");

        UnmodifiableSpanletProcessor spanletEventLoop = new UnmodifiableSpanletProcessor("prefix",
                unmodifiableSpanlet,config,5,1, Map.of());

        MapSignalDestination finalDestination = new MapSignalDestination("final", Signal.class);

        spanletEventLoop.connect(finalDestination);

        spanletEventLoop.start();

        Span span = Span.newBuilder().setName("span-name").build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter().recycle(span, null, null);


        assertTrue(spanletEventLoop.send(protoSpanAdapter));

        spanletEventLoop.stop();

        assertEquals("prefix-span-name",spanletEventLoop.getContext().getAttribute("name-with-prefix",String.class));

    }

    public static class SpanletConfig {
        private final String prefix;

        public SpanletConfig(String prefix) {
            this.prefix = prefix;
        }
    }

    public static class UnmodifiableSpanletTest implements com.siglet.api.unmodifiable.trace.UnmodifiableSpanlet<SpanletConfig> {

        @Override
        public Result span(UnmodifiableSpan span, ProcessorContext<SpanletConfig> processorContext,
                           ResultFactory resultFactory) {
            processorContext.setAttribute("name-with-prefix",processorContext.getConfig().prefix + span.getName());
            return resultFactory.proceed();
        }
    }

}