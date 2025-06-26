package com.siglet.container.adapter.metric;

import com.siglet.SigletError;
import com.siglet.api.modifiable.metric.ModifiableData;
import com.siglet.api.modifiable.metric.ModifiableMetric;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.common.ProtoInstrumentationScopeAdapter;
import com.siglet.container.adapter.common.ProtoResourceAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoMetricAdapter extends Adapter<Metric, Metric.Builder> implements ModifiableMetric {


    private Resource protoResource;

    private InstrumentationScope protoInstrumentationScope;

    private ProtoResourceAdapter protoResourceAdapter;

    private ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;

    private ProtoGaugeAdapter protoGaugeAdapter;

    private ProtoSumAdapter protoSumAdapter;

    private ProtoHistogramAdapter protoHistogramAdapter;

    private ProtoExponentialHistogramAdapter protoExponentialHistogramAdapter;

    private ProtoSummaryAdapter protoSummaryAdapter;


    public ProtoMetricAdapter(Metric protoMetric, Resource protoResource,
                              InstrumentationScope protoInstrumentationScope) {
        super(protoMetric, Metric::toBuilder, Metric.Builder::build);
        this.protoResource = protoResource;
        this.protoInstrumentationScope = protoInstrumentationScope;
    }

    public ProtoMetricAdapter() {
        super(Metric.newBuilder(), Metric.Builder::build);
    }

    public ProtoMetricAdapter(Resource protoResource, InstrumentationScope protoInstrumentationScope) {
        super(Metric.newBuilder(), Metric.Builder::build);
        this.protoResource = protoResource;
        this.protoInstrumentationScope = protoInstrumentationScope;
    }

    @Override
    public String getName() {
        return getValue(Metric::getName, Metric.Builder::getName);
    }

    @Override
    public ProtoMetricAdapter setName(String name) {
        setValue(Metric.Builder::setName, name);
        return this;
    }

    @Override
    public String getDescription() {
        return getValue(Metric::getDescription, Metric.Builder::getDescription);
    }

    @Override
    public ProtoMetricAdapter setDescription(String description) {
        setValue(Metric.Builder::setDescription, description);
        return this;
    }

    @Override
    public String getUnit() {
        return getValue(Metric::getUnit, Metric.Builder::getUnit);
    }

    @Override
    public ProtoMetricAdapter setUnit(String unit) {
        setValue(Metric.Builder::setUnit, unit);
        return this;
    }

    public ProtoGaugeAdapter gauge() {
        protoGaugeAdapter = new ProtoGaugeAdapter();
        return protoGaugeAdapter;
    }

    public ProtoSumAdapter sum() {
        protoSumAdapter = new ProtoSumAdapter();
        return protoSumAdapter;
    }

    @Override
    public ModifiableData getData() {
        if (hasGauge()) {
            if (protoGaugeAdapter == null) {
                protoGaugeAdapter = new ProtoGaugeAdapter(getMessage().getGauge());
            }
            return protoGaugeAdapter;
        } else if (hasSum()) {
            if (protoSumAdapter == null) {
                protoSumAdapter = new ProtoSumAdapter(getMessage().getSum());
            }
            return protoSumAdapter;
        } else if (hasHistogram()) {
            if (protoHistogramAdapter == null) {
                protoHistogramAdapter = new ProtoHistogramAdapter(getMessage().getHistogram());
            }
            return protoHistogramAdapter;
        } else if (hasExponentialHistogram()) {
            if (protoExponentialHistogramAdapter == null) {
                protoExponentialHistogramAdapter =
                        new ProtoExponentialHistogramAdapter(getMessage().getExponentialHistogram());
            }
            return protoExponentialHistogramAdapter;
        } else if (hasSummary()) {
            if (protoSummaryAdapter == null) {
                protoSummaryAdapter = new ProtoSummaryAdapter(getMessage().getSummary());
            }
            return protoSummaryAdapter;
        }
        throw new SigletError("invalid metric type!");
    }

    @Override
    public boolean hasGauge() {
        return protoGaugeAdapter != null || getValue(Metric::hasGauge, Metric.Builder::hasGauge);
    }

    @Override
    public ProtoGaugeAdapter getGauge() {
        if (!hasGauge()) {
            throw new SigletError("data is not a gauge ");
        } else {
            return (ProtoGaugeAdapter) getData();
        }
    }

    public boolean hasSum() {
        return protoSumAdapter != null || getValue(Metric::hasSum, Metric.Builder::hasSum);
    }

    public ProtoSumAdapter getSum() {
        if (!hasSum()) {
            throw new SigletError("data is not a sum ");
        } else {
            return (ProtoSumAdapter) getData();
        }
    }

    public boolean hasHistogram() {
        return getValue(Metric::hasHistogram, Metric.Builder::hasHistogram);
    }

    public ProtoHistogramAdapter getHistogram() {
        if (!hasHistogram()) {
            throw new SigletError("data is not a histogram ");
        } else {
            return (ProtoHistogramAdapter) getData();
        }
    }

    public boolean hasExponentialHistogram() {
        return getValue(Metric::hasExponentialHistogram, Metric.Builder::hasExponentialHistogram);
    }

    public ProtoExponentialHistogramAdapter getExponentialHistogram() {
        if (!hasExponentialHistogram()) {
            throw new SigletError("data is not a exponential histogram ");
        } else {
            return (ProtoExponentialHistogramAdapter) getData();
        }
    }

    public boolean hasSummary() {
        return getValue(Metric::hasSummary, Metric.Builder::hasSummary);
    }

    public ProtoSummaryAdapter getSummary() {
        if (!hasSummary()) {
            throw new SigletError("data is not a summary ");
        } else {
            return (ProtoSummaryAdapter) getData();
        }
    }

    public ProtoResourceAdapter getProtoResourceAdapter() {
        if (protoResourceAdapter != null) {
            protoResourceAdapter = new ProtoResourceAdapter(protoResource);
        }
        return protoResourceAdapter;
    }

    public ProtoInstrumentationScopeAdapter getProtoInstrumentationScopeAdapter() {
        if (protoInstrumentationScopeAdapter != null) {
            protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(protoInstrumentationScope);
        }
        return protoInstrumentationScopeAdapter;
    }

    public Resource getUpdatedResource() {
        if (protoResourceAdapter == null || !protoResourceAdapter.isUpdated()) {
            return protoResource;
        } else {
            return protoResourceAdapter.getUpdated();
        }
    }

    public InstrumentationScope getUpdatedInstrumentationScope() {
        if (protoInstrumentationScopeAdapter == null || !protoInstrumentationScopeAdapter.isUpdated()) {
            return protoInstrumentationScope;
        } else {
            return protoInstrumentationScopeAdapter.getUpdated();
        }
    }


    @Override
    protected void enrich(Metric.Builder builder) {
        if (protoGaugeAdapter != null && protoGaugeAdapter.isUpdated()) {
            builder.setGauge(protoGaugeAdapter.getUpdated());
        }
    }

    @Override
    public String getId() {
        throw new IllegalStateException("to be implemented");
    }
}
