package com.siglet.container.engine.pipeline.processor.siglet.spanlet.unmodifiable;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpan;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpanlet;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.pool.SpanObjectPool;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.engine.Context;
import com.siglet.container.eventloop.processor.ProcessorContextImpl;
import com.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnmodifiedSpanBaseEventloopProcessorTest {

    private Span span;

    private InstrumentationScope scope;

    private Resource resource;

    private ProtoSpanAdapter protoSpanAdapter;

    private SpanObjectPool spanObjectPool;

    private Context context;

    private UnmodifiedSpanBaseEventloopProcessor unmodifiedSpanBaseEventloopProcessor;

    private MockSpanletModifySpan mockSpanletModifySpan;

    @BeforeEach
    void setUp() {
        span = Span.newBuilder()
                .setName("span-name")
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(1))
                .addAttributes(KeyValue.newBuilder()
                        .setKey("attribute-key")
                        .setValue(AnyValue.newBuilder().setStringValue("attribute-value").build())
                        .build())
                .build();

        scope = InstrumentationScope.newBuilder()
                .setName("scope-name")
                .build();

        resource = Resource.newBuilder()
                .setDroppedAttributesCount(1)
                .build();

        spanObjectPool = new SpanObjectPool(0);
        context = new Context(null, new SpanObjectPool(0), null, null);

        mockSpanletModifySpan = new MockSpanletModifySpan();

        unmodifiedSpanBaseEventloopProcessor = new UnmodifiedSpanBaseEventloopProcessor<>(context,
                new ProcessorContextImpl<Void>(null), mockSpanletModifySpan);
    }


    @Test
    void process_nonModifiedSpan() {
        protoSpanAdapter = spanObjectPool.get(span, scope, resource);
        protoSpanAdapter.setName("changed-span");
        unmodifiedSpanBaseEventloopProcessor.process(protoSpanAdapter, new ProcessorContextImpl<Void>(null),
                ResultFactoryImpl.INSTANCE);

        assertEquals(0, spanObjectPool.size());


    }

    static class MockSpanletModifySpan implements UnmodifiableSpanlet<Void> {

        public boolean called;

        @Override
        public Result span(UnmodifiableSpan unmodifiableSpan, ProcessorContext<Void> processorContext, ResultFactory resultFactory) {
            ((ProtoSpanAdapter) unmodifiableSpan).setName("new-name");
            called = true;
            return resultFactory.proceed();
        }
    }

    static class MockSpanletNotModifySpan implements UnmodifiableSpanlet<Void> {

        public boolean called;

        @Override
        public Result span(UnmodifiableSpan unmodifiableSpan, ProcessorContext<Void> processorContext, ResultFactory resultFactory) {
            called = true;
            return resultFactory.proceed();
        }
    }
}