package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableExponentialHistogram;
import com.siglet.api.unmodifiable.metric.AggregationTemporality;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogram;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

import java.util.List;

public class ProtoExponentialHistogramAdapter extends Adapter<ExponentialHistogram, ExponentialHistogram.Builder>
        implements ModifiableExponentialHistogram {

    public ProtoExponentialHistogramAdapter() {
        super(AdapterConfig.EXPONENTIAL_HISTOGRAM_ADAPTER_CONFIG,
                List.of(),
                List.of(AdapterListConfig.EXPONENTIAL_HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG));
        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, dataPoints -> {
            getBuilder().clearDataPoints();
            getBuilder().addAllDataPoints((Iterable<? extends ExponentialHistogramDataPoint>) dataPoints);
        });
    }


    @Override
    public ProtoExponentialHistogramDataPointsAdapter getDataPoints() {
        return getAdapterList(
                AdapterListConfig.EXPONENTIAL_HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG,
                this::getDataPointsList);
    }

    protected List<ExponentialHistogramDataPoint> getDataPointsList() {
        return getValue(ExponentialHistogram::getDataPointsList, ExponentialHistogram.Builder::getDataPointsList);
    }

    @Override
    public AggregationTemporality getAggregationTemporality() {
        return AdapterUtils.valueOf(
                getValue(ExponentialHistogram::getAggregationTemporality, ExponentialHistogram.Builder::getAggregationTemporality));
    }

    @Override
    public ProtoExponentialHistogramAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        setValue(ExponentialHistogram.Builder::setAggregationTemporality, AdapterUtils.valueOf(aggregationTemporality));
        return this;
    }

}
