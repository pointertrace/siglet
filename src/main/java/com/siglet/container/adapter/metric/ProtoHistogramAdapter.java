package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableHistogram;
import com.siglet.api.unmodifiable.metric.AggregationTemporality;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.Histogram;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;

import java.util.List;

public class ProtoHistogramAdapter extends Adapter<Histogram, Histogram.Builder> implements ModifiableHistogram {


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
