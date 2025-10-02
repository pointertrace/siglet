package io.github.pointertrace.siglet.impl.adapter.pool;

import io.github.pointertrace.siglet.impl.adapter.metric.ProtoMetricAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.resource.v1.Resource;

public class MetricObjectPool extends BaseObjectPool<ProtoMetricAdapter> {

    public MetricObjectPool(int initialSize) {
        super(initialSize, ProtoMetricAdapter::new);
    }

    public synchronized ProtoMetricAdapter get(Metric metric, InstrumentationScope scope, Resource resource) {
        ProtoMetricAdapter adapter = super.get();
        adapter.recycle(metric, resource, scope);
        return adapter;
    }
}
