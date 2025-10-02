package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import io.github.pointertrace.siglet.impl.adapter.Adapter;
import io.github.pointertrace.siglet.impl.adapter.AdapterConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.Histogram;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;

import java.util.List;

public class ProtoHistogramAdapter extends Adapter<Histogram, Histogram.Builder>
        implements io.github.pointertrace.siglet.api.signal.metric.Histogram {


    public ProtoHistogramAdapter() {
        super(AdapterConfig.HISTOGRAM_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.NUMBER_DATA_POINTS_ADAPTER_CONFIG, dataPoints -> {
            getBuilder().clearDataPoints();
            getBuilder().addAllDataPoints((Iterable<HistogramDataPoint>) dataPoints);
        });
    }

    @Override
    public ProtoHistogramDataPointsAdapter getDataPoints() {
        return getAdapterList(AdapterListConfig.HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG,
                this::getDataPointsList);
    }

    public List<HistogramDataPoint> getDataPointsList() {
        return getValue(Histogram::getDataPointsList, Histogram.Builder::getDataPointsList);
    }

    @Override
    public AggregationTemporality getAggregationTemporality() {
        return AdapterUtils.valueOf(
                getValue(Histogram::getAggregationTemporality, Histogram.Builder::getAggregationTemporality));
    }

    @Override
    public ProtoHistogramAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        setValue(Histogram.Builder::setAggregationTemporality, AdapterUtils.valueOf(aggregationTemporality));
        return this;
    }

}
