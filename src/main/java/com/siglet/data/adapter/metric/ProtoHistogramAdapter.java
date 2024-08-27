package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.modifiable.metric.ModifiableHistogram;
import com.siglet.data.modifiable.metric.ModifiableSum;
import com.siglet.data.unmodifiable.metric.AggregationTemporality;
import io.opentelemetry.proto.metrics.v1.Histogram;
import io.opentelemetry.proto.metrics.v1.Sum;

public class ProtoHistogramAdapter extends Adapter<Histogram, Histogram.Builder> implements ModifiableHistogram {

    private ProtoHistogramDataPointsAdapter protoHistogramDataPointsAdapter;

    public ProtoHistogramAdapter(Histogram protoHistogram, boolean updatable) {
        super(protoHistogram, Histogram::toBuilder, Histogram.Builder::build, updatable);
    }

    public ProtoHistogramAdapter(Histogram.Builder histogramBuilder) {
        super(histogramBuilder, Histogram.Builder::build);
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoHistogramDataPointsAdapter != null && protoHistogramDataPointsAdapter.isUpdated());
    }

    @Override
    public ProtoHistogramDataPointsAdapter getDataPoints() {
        if (protoHistogramDataPointsAdapter == null) {
            protoHistogramDataPointsAdapter = new ProtoHistogramDataPointsAdapter(
                    getValue(Histogram::getDataPointsList, Histogram.Builder::getDataPointsList), isUpdatable());
        }
        return protoHistogramDataPointsAdapter;
    }

    @Override
    protected void enrich(Histogram.Builder builder) {
        if (protoHistogramDataPointsAdapter != null && protoHistogramDataPointsAdapter.isUpdated()) {
            builder.clearDataPoints();
            builder.addAllDataPoints(protoHistogramDataPointsAdapter.getUpdated());
        }
    }

    @Override
    public AggregationTemporality getAggregationTemporality() {
        return AggregationTemporality.valueOf(
                getValue(Histogram::getAggregationTemporality, Histogram.Builder::getAggregationTemporality));
    }

    @Override
    public ProtoHistogramAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        setValue(Histogram.Builder::setAggregationTemporality, AggregationTemporality.toProto(aggregationTemporality));
        return this;
    }

}
