package com.siglet.container.adapter.pool;

import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BasePoolTest {

    private SpanCreator spanCreator;

    private SpanPool spanPool;

    private Span span1;

    private InstrumentationScope scope1;

    private Resource resource1;

    private Span span2;

    private InstrumentationScope scope2;

    private Resource resource2;

    @BeforeEach
    public void setUp() {
        spanCreator = new SpanCreator();

        span1 = Span.newBuilder()
                .setName("span 1 name")
                .build();

        span2 = Span.newBuilder()
                .setName("span 2 name")
                .build();

        scope1 = InstrumentationScope.newBuilder()
                .setName("instrumentation scope 1 name")
                .build();

        scope2 = InstrumentationScope.newBuilder()
                .setName("instrumentation scope 2 name")
                .build();

        resource1 = Resource.newBuilder()
                .addAttributes(KeyValue.newBuilder()
                        .setKey("resource 1 attribute key")
                        .setValue(AnyValue.newBuilder()
                                .setStringValue("resource attribute value")
                                .build())
                        .build())
                .setDroppedAttributesCount(10)
                .build();

        resource2 = Resource.newBuilder()
                .addAttributes(KeyValue.newBuilder()
                        .setKey("resource 2 attribute key")
                        .setValue(AnyValue.newBuilder()
                                .setStringValue("resource attribute value")
                                .build())
                        .build())
                .setDroppedAttributesCount(10)
                .build();
    }


    @Test
    void get_recycling() {

        spanPool = new SpanPool(1, spanCreator);

        ProtoSpanAdapter spanAdapter = spanPool.get(span1, scope1, resource1);

        assertEquals(1, spanCreator.numCreatedObjects);

        assertSame(span1, spanAdapter.getUpdated());
        assertSame(scope1, spanAdapter.getUpdatedInstrumentationScope());
        assertSame(resource1, spanAdapter.getUpdatedResource());

        spanPool.recycle(spanAdapter);


        spanAdapter = spanPool.get(span2, scope2, resource2);

        assertEquals(1, spanCreator.numCreatedObjects);

        assertSame(span2, spanAdapter.getUpdated());
        assertSame(scope2, spanAdapter.getUpdatedInstrumentationScope());
        assertSame(resource2, spanAdapter.getUpdatedResource());

    }

    @Test
    void get() {

        spanPool = new SpanPool(1, spanCreator);

        ProtoSpanAdapter spanAdapter = spanPool.get(span1, scope1, resource1);

        assertEquals(1, spanCreator.numCreatedObjects);

        assertSame(span1, spanAdapter.getUpdated());
        assertSame(scope1, spanAdapter.getUpdatedInstrumentationScope());
        assertSame(resource1, spanAdapter.getUpdatedResource());

        spanAdapter = spanPool.get(span2, scope2, resource2);

        assertEquals(2, spanCreator.numCreatedObjects);

        assertSame(span2, spanAdapter.getUpdated());
        assertSame(scope2, spanAdapter.getUpdatedInstrumentationScope());
        assertSame(resource2, spanAdapter.getUpdatedResource());

    }

    static class SpanPool extends BasePool<ProtoSpanAdapter> {

        public SpanPool(int initialSize, Supplier<ProtoSpanAdapter> supplier) {
            super(initialSize, supplier);
        }

        public ProtoSpanAdapter get(Span span, InstrumentationScope scope, Resource resource) {
            ProtoSpanAdapter object = super.get();
            object.recycle(span, resource, scope);
            return object;
        }
    }

    static class SpanCreator implements Supplier<ProtoSpanAdapter> {

        private int numCreatedObjects;

        @Override
        public ProtoSpanAdapter get() {
            numCreatedObjects++;
            return new ProtoSpanAdapter();
        }
    }


}