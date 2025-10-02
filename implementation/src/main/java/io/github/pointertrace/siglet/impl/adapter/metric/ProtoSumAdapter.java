package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import io.github.pointertrace.siglet.impl.adapter.Adapter;
import io.github.pointertrace.siglet.impl.adapter.AdapterConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.Sum;

import java.util.List;

public class ProtoSumAdapter extends Adapter<Sum, Sum.Builder>
        implements io.github.pointertrace.siglet.api.signal.metric.Sum {

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
    public boolean getMonotonic() {
        return getValue(Sum::getIsMonotonic, Sum.Builder::getIsMonotonic);
    }


    @Override
    public ProtoSumAdapter setMonotonic(boolean monotonic) {
        setValue(Sum.Builder::setIsMonotonic, monotonic);
        return this;
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

}
