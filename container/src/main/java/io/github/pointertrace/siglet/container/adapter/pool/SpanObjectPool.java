package io.github.pointertrace.siglet.container.adapter.pool;

import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;

public class SpanObjectPool extends BaseObjectPool<ProtoSpanAdapter> {

    public SpanObjectPool(int initialSize) {
        super(initialSize, ProtoSpanAdapter::new);
    }

    public synchronized ProtoSpanAdapter get(Span span, InstrumentationScope scope, Resource resource) {
        ProtoSpanAdapter adapter = super.get();
        adapter.recycle(span, resource, scope);
        return adapter;
    }
}
