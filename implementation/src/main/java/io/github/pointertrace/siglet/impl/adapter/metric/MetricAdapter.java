package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.Data;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.impl.adapter.InstrumentationScopeAdapter;
import io.github.pointertrace.siglet.impl.adapter.ResourceAdapter;

import java.util.Objects;

public final class MetricAdapter implements Metric {
    private final io.opentelemetry.proto.metrics.v1.Metric original;
    private io.opentelemetry.proto.metrics.v1.Metric.Builder builder;
    private final io.opentelemetry.proto.resource.v1.Resource resource;
    private final io.opentelemetry.proto.common.v1.InstrumentationScope scope;
    private ResourceAdapter resourceAdapter;
    private InstrumentationScopeAdapter scopeAdapter;
    private GaugeAdapter gaugeAdapter;
    private SumAdapter sumAdapter;
    private HistogramAdapter histogramAdapter;
    private ExponentialHistogramAdapter exponentialHistogramAdapter;
    private SummaryAdapter summaryAdapter;

    public MetricAdapter(io.opentelemetry.proto.metrics.v1.Metric metric,
                         io.opentelemetry.proto.resource.v1.Resource resource,
                         io.opentelemetry.proto.common.v1.InstrumentationScope scope) {
        this.original = Objects.requireNonNull(metric, "metric");
        this.resource = Objects.requireNonNull(resource, "resource");
        this.scope = Objects.requireNonNull(scope, "scope");
    }

    private io.opentelemetry.proto.metrics.v1.Metric.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.MetricOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.Metric getUpdated() {
        return builder == null ? original : builder.build();
    }

    public io.opentelemetry.proto.resource.v1.Resource getUpdatedResource() {
        return getResourceAdapter().getUpdated();
    }

    public io.opentelemetry.proto.common.v1.InstrumentationScope getUpdatedScope() {
        return getScopeAdapter().getUpdated();
    }

    private ResourceAdapter getResourceAdapter() {
        if (resourceAdapter == null) {
            resourceAdapter = new ResourceAdapter(resource);
        }
        return resourceAdapter;
    }

    private InstrumentationScopeAdapter getScopeAdapter() {
        if (scopeAdapter == null) {
            scopeAdapter = new InstrumentationScopeAdapter(scope);
        }
        return scopeAdapter;
    }

    public MetricAdapter duplicate() {
        return new MetricAdapter(getUpdated(), getUpdatedResource(), getUpdatedScope());
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public String getName() {
        return view().getName();
    }

    @Override
    public MetricAdapter setName(String name) {
        mutate().setName(Objects.requireNonNull(name, "name"));
        return this;
    }

    @Override
    public String getDescription() {
        return view().getDescription();
    }

    @Override
    public MetricAdapter setDescription(String description) {
        mutate().setDescription(Objects.requireNonNull(description, "description"));
        return this;
    }

    @Override
    public String getUnit() {
        return view().getUnit();
    }

    @Override
    public MetricAdapter setUnit(String unit) {
        mutate().setUnit(Objects.requireNonNull(unit, "unit"));
        return this;
    }

    @Override
    public Data getData() {
        if (hasGauge()) {
            return getGauge();
        }
        if (hasSum()) {
            return getSum();
        }
        if (hasHistogram()) {
            return getHistogram();
        }
        if (hasExponentialHistogram()) {
            return getExponentialHistogram();
        }
        if (hasSummary()) {
            return getSummary();
        }
        return null;
    }

    @Override
    public boolean hasGauge() {
        return view().hasGauge();
    }

    @Override
    public GaugeAdapter getGauge() {
        if (gaugeAdapter == null) {
            gaugeAdapter = new GaugeAdapter(view().getGauge(), updated -> mutate().setGauge(updated));
        }
        return gaugeAdapter;
    }

    @Override
    public boolean hasSum() {
        return view().hasSum();
    }

    @Override
    public SumAdapter getSum() {
        if (sumAdapter == null) {
            sumAdapter = new SumAdapter(view().getSum(), updated -> mutate().setSum(updated));
        }
        return sumAdapter;
    }

    @Override
    public boolean hasHistogram() {
        return view().hasHistogram();
    }

    @Override
    public HistogramAdapter getHistogram() {
        if (histogramAdapter == null) {
            histogramAdapter = new HistogramAdapter(view().getHistogram(), updated -> mutate().setHistogram(updated));
        }
        return histogramAdapter;
    }

    @Override
    public boolean hasExponentialHistogram() {
        return view().hasExponentialHistogram();
    }

    @Override
    public ExponentialHistogramAdapter getExponentialHistogram() {
        if (exponentialHistogramAdapter == null) {
            exponentialHistogramAdapter = new ExponentialHistogramAdapter(view().getExponentialHistogram(), updated -> mutate().setExponentialHistogram(updated));
        }
        return exponentialHistogramAdapter;
    }

    @Override
    public boolean hasSummary() {
        return view().hasSummary();
    }

    @Override
    public SummaryAdapter getSummary() {
        if (summaryAdapter == null) {
            summaryAdapter = new SummaryAdapter(view().getSummary(), updated -> mutate().setSummary(updated));
        }
        return summaryAdapter;
    }

    public ResourceAdapter getResource() {
        return getResourceAdapter();
    }

    public InstrumentationScopeAdapter getInstrumentationScope() {
        return getScopeAdapter();
    }
}
