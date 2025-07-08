package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableSum;
import com.siglet.api.unmodifiable.metric.AggregationTemporality;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.Sum;

import java.util.List;

public class ProtoSumAdapter extends Adapter<Sum, Sum.Builder> implements ModifiableSum {

    public ProtoSumAdapter() {
        super(AdapterConfig.SUM_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.NUMBER_DATA_POINTS_ADAPTER_CONFIG, dataPoints -> {
            getBuilder().clearDataPoints();
            getBuilder().addAllDataPoints((Iterable<NumberDataPoint>) dataPoints);
        });
    }

    protected List<NumberDataPoint> getDataPointsList() {
        return getValue(Sum::getDataPointsList, Sum.Builder::getDataPointsList);
    }

    @Override
    public ProtoNumberDataPointsAdapter getDataPoints() {
        return  getAdapterList(AdapterListConfig.NUMBER_DATA_POINTS_ADAPTER_CONFIG, this::getDataPointsList);
    }

    @Override
    public boolean isMonotonic() {
        return getValue(Sum::getIsMonotonic, Sum.Builder::getIsMonotonic);
    }

    @Override
    public AggregationTemporality getAggregationTemporality() {
        return AdapterUtils.valueOf(
                getValue(Sum::getAggregationTemporality, Sum.Builder::getAggregationTemporality));
    }

    @Override
    public ProtoSumAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        setValue(Sum.Builder::setAggregationTemporality, AdapterUtils.valueOf(aggregationTemporality));
        return this;
    }

    @Override
    public ProtoSumAdapter setMonotonic(boolean monotonic) {
        setValue(Sum.Builder::setIsMonotonic, monotonic);
        return this;
    }
}
