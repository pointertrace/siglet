package com.siglet.container.adapter.pool;

import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.resource.v1.Resource;

public class MetricPool extends BasePool<ProtoMetricAdapter> {

    public MetricPool(int initialSize) {
        super(initialSize, ProtoMetricAdapter::new);
    }

    public synchronized ProtoMetricAdapter get(Metric metric, InstrumentationScope scope, Resource resource) {
        ProtoMetricAdapter adapter = super.get();
        adapter.recycle(metric, resource, scope);
        return adapter;
    }
}
