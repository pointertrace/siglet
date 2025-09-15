package io.github.pointertrace.siglet.container.adapter.metric;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.api.signal.metric.Data;
import io.github.pointertrace.siglet.container.adapter.Adapter;
import io.github.pointertrace.siglet.container.adapter.AdapterConfig;
import io.github.pointertrace.siglet.container.adapter.common.ProtoInstrumentationScopeAdapter;
import io.github.pointertrace.siglet.container.adapter.common.ProtoResourceAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.*;
import io.opentelemetry.proto.resource.v1.Resource;

import java.util.UUID;

public class ProtoMetricAdapter extends Adapter<Metric, Metric.Builder> implements io.github.pointertrace.siglet.api.signal.metric.Metric {

    private UUID id;

    private Resource protoResource;

    private InstrumentationScope protoInstrumentationScope;

    private ProtoResourceAdapter protoResourceAdapter;

    private ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;

    public ProtoMetricAdapter() {
        super(AdapterConfig.METRIC_ADAPTER_CONFIG);
        addEnricher(AdapterConfig.GAUGE_ADAPTER_CONFIG, gauge -> getBuilder().setGauge((Gauge) gauge));
        addEnricher(AdapterConfig.SUM_ADAPTER_CONFIG, sum -> getBuilder().setSum((Sum) sum));
        addEnricher(AdapterConfig.HISTOGRAM_ADAPTER_CONFIG, histogram -> getBuilder().setHistogram((Histogram) histogram));
        addEnricher(AdapterConfig.EXPONENTIAL_HISTOGRAM_ADAPTER_CONFIG,
                exponentialHistogram -> getBuilder().setExponentialHistogram((ExponentialHistogram) exponentialHistogram));
        addEnricher(AdapterConfig.SUMMARY_ADAPTER_CONFIG, summary -> getBuilder().setSummary((Summary) summary));
    }

    public ProtoMetricAdapter recycle(Metric protoMetric, Resource protoResource,
                                      InstrumentationScope protoInstrumentationScope) {
        super.recycle(protoMetric);
        this.protoResource = protoResource;
        this.protoInstrumentationScope = protoInstrumentationScope;
        this.id = UUID.randomUUID();
        return this;
    }


    public ProtoMetricAdapter recycle(Resource protoResource, InstrumentationScope protoInstrumentationScope) {
        super.recycle(Metric.newBuilder());
        this.protoResource = protoResource;
        this.protoInstrumentationScope = protoInstrumentationScope;
        return this;
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

    protected Gauge getGaugeProto() {
        return getValue(Metric::getGauge, Metric.Builder::getGauge);
    }

    public ProtoGaugeAdapter gauge() {
        return getAdapter(AdapterConfig.GAUGE_ADAPTER_CONFIG, this::getGaugeProto);
    }

    protected Sum getSumProto() {
        return getValue(Metric::getSum, Metric.Builder::getSum);
    }

    public ProtoSumAdapter sum() {
        return getAdapter(AdapterConfig.SUM_ADAPTER_CONFIG, this::getSumProto);
    }

    protected Histogram getHistogramProto() {
        return getValue(Metric::getHistogram, Metric.Builder::getHistogram);
    }

    public ProtoHistogramAdapter histogram() {
        return getAdapter(AdapterConfig.HISTOGRAM_ADAPTER_CONFIG, this::getHistogramProto);
    }

    protected ExponentialHistogram getExponentialHistogramProto() {
        return getValue(Metric::getExponentialHistogram, Metric.Builder::getExponentialHistogram);
    }

    public ProtoExponentialHistogramAdapter exponentialHistogram() {
        return getAdapter(AdapterConfig.EXPONENTIAL_HISTOGRAM_ADAPTER_CONFIG, this::getExponentialHistogramProto);
    }

    protected Summary getSummaryProto() {
        return getValue(Metric::getSummary, Metric.Builder::getSummary);
    }

    public ProtoSummaryAdapter summary() {
        return getAdapter(AdapterConfig.SUMMARY_ADAPTER_CONFIG, this::getSummaryProto);
    }

    @Override
    public Data getData() {
        if (hasGauge()) {
            return gauge();
        } else if (hasSum()) {
            return sum();
        } else if (hasHistogram()) {
            return histogram();
        } else if (hasExponentialHistogram()) {
            return exponentialHistogram();
        } else if (hasSummary()) {
            return summary();
        }
        throw new SigletError("invalid metric type!");
    }

    @Override
    public boolean hasGauge() {
        return getDataCase().equals(Metric.DataCase.GAUGE);
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
        return getDataCase().equals(Metric.DataCase.SUM);
    }

    public ProtoSumAdapter getSum() {
        if (!hasSum()) {
            throw new SigletError("data is not a sum ");
        } else {
            return (ProtoSumAdapter) getData();
        }
    }

    public boolean hasHistogram() {
        return getDataCase().equals(Metric.DataCase.HISTOGRAM);
    }

    public ProtoHistogramAdapter getHistogram() {
        if (!hasHistogram()) {
            throw new SigletError("data is not a histogram ");
        } else {
            return (ProtoHistogramAdapter) getData();
        }
    }

    public boolean hasExponentialHistogram() {
        return getDataCase().equals(Metric.DataCase.EXPONENTIAL_HISTOGRAM);
    }

    public ProtoExponentialHistogramAdapter getExponentialHistogram() {
        if (!hasExponentialHistogram()) {
            throw new SigletError("data is not a exponential histogram ");
        } else {
            return (ProtoExponentialHistogramAdapter) getData();
        }
    }

    public boolean hasSummary() {
        return getDataCase().equals(Metric.DataCase.SUMMARY);
    }

    public ProtoSummaryAdapter getSummary() {
        if (!hasSummary()) {
            throw new SigletError("data is not a summary ");
        } else {
            return (ProtoSummaryAdapter) getData();
        }
    }

    public Metric.DataCase getDataCase() {
        return getValue(Metric::getDataCase, Metric.Builder::getDataCase);

    }

    public ProtoResourceAdapter getProtoResourceAdapter() {
        if (protoResourceAdapter != null) {
            protoResourceAdapter = new ProtoResourceAdapter();
            protoResourceAdapter.recycle(protoResource);
        }
        return protoResourceAdapter;
    }

    // TODO não testado
    public ProtoInstrumentationScopeAdapter getProtoInstrumentationScopeAdapter() {
        if (protoInstrumentationScopeAdapter != null) {
            protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter();
            protoInstrumentationScopeAdapter.recycle(protoInstrumentationScope);
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
    public String getId() {
        return "Metric(" + switch (getDataCase()) {
            case SUM -> "sum-";
            case GAUGE -> "gauge-";
            case HISTOGRAM -> "histogram-";
            case EXPONENTIAL_HISTOGRAM -> "exponential-histogram-";
            case SUMMARY -> "summary-";
            default -> "unknown-";
        } + id + ")";
    }
}
