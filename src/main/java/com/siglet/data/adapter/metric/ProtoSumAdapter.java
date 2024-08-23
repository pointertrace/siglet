package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.modifiable.metric.ModifiableSum;
import com.siglet.data.unmodifiable.metric.AggregationTemporality;
import io.opentelemetry.proto.metrics.v1.Sum;

public class ProtoSumAdapter extends Adapter<Sum, Sum.Builder> implements ModifiableSum {

    private ProtoNumberDataPointsAdapter protoNumberDataPointsAdapter;

    public ProtoSumAdapter(Sum protoSum, boolean updatable) {
        super(protoSum, Sum::toBuilder, Sum.Builder::build, updatable);
    }

    public ProtoSumAdapter(Sum.Builder sumBuilder) {
        super(sumBuilder, Sum.Builder::build);
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoNumberDataPointsAdapter != null && protoNumberDataPointsAdapter.isUpdated());
    }

    @Override
    public ProtoNumberDataPointsAdapter getDataPoints() {
        if (protoNumberDataPointsAdapter == null) {
            protoNumberDataPointsAdapter = new ProtoNumberDataPointsAdapter(
                    getValue(Sum::getDataPointsList, Sum.Builder::getDataPointsList), isUpdatable());
        }
        return protoNumberDataPointsAdapter;
    }

    @Override
    protected void enrich(Sum.Builder builder) {
        if (protoNumberDataPointsAdapter != null && protoNumberDataPointsAdapter.isUpdated()) {
            builder.clearDataPoints();
            builder.addAllDataPoints(protoNumberDataPointsAdapter.getUpdated());
        }
    }

    @Override
    public boolean isMonotonic() {
        return getValue(Sum::getIsMonotonic, Sum.Builder::getIsMonotonic);
    }

    @Override
    public AggregationTemporality getAggregationTemporality() {
        return AggregationTemporality.valueOf(
                getValue(Sum::getAggregationTemporality, Sum.Builder::getAggregationTemporality));
    }

    @Override
    public ProtoSumAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        setValue(Sum.Builder::setAggregationTemporality, AggregationTemporality.toProto(aggregationTemporality));
        return this;
    }

    @Override
    public ProtoSumAdapter setMonotonic(boolean monotonic) {
        setValue(Sum.Builder::setIsMonotonic, monotonic);
        return this;
    }
}
