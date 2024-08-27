package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.modifiable.metric.ModifiableExponentialHistogram;
import com.siglet.data.modifiable.metric.ModifiableHistogram;
import com.siglet.data.unmodifiable.metric.AggregationTemporality;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogram;
import io.opentelemetry.proto.metrics.v1.Histogram;

public class ProtoExponentialHistogramAdapter extends Adapter<ExponentialHistogram, ExponentialHistogram.Builder>
        implements ModifiableExponentialHistogram {

    private ProtoExponentialHistogramDataPointsAdapter protoExponentialHistogramDataPointsAdapter;

    public ProtoExponentialHistogramAdapter(ExponentialHistogram protoHistogram, boolean updatable) {
        super(protoHistogram, ExponentialHistogram::toBuilder, ExponentialHistogram.Builder::build, updatable);
    }

    public ProtoExponentialHistogramAdapter(ExponentialHistogram.Builder histogramBuilder) {
        super(histogramBuilder, ExponentialHistogram.Builder::build);
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() ||
                (protoExponentialHistogramDataPointsAdapter != null &&
                        protoExponentialHistogramDataPointsAdapter.isUpdated());
    }

    @Override
    public ProtoExponentialHistogramDataPointsAdapter getDataPoints() {
        if (protoExponentialHistogramDataPointsAdapter == null) {
            protoExponentialHistogramDataPointsAdapter = new ProtoExponentialHistogramDataPointsAdapter(
                    getValue(ExponentialHistogram::getDataPointsList, ExponentialHistogram.Builder::getDataPointsList), isUpdatable());
        }
        return protoExponentialHistogramDataPointsAdapter;
    }

    @Override
    protected void enrich(ExponentialHistogram.Builder builder) {
        if (protoExponentialHistogramDataPointsAdapter != null && protoExponentialHistogramDataPointsAdapter.isUpdated()) {
            builder.clearDataPoints();
            builder.addAllDataPoints(protoExponentialHistogramDataPointsAdapter.getUpdated());
        }
    }

    @Override
    public AggregationTemporality getAggregationTemporality() {
        return AggregationTemporality.valueOf(
                getValue(ExponentialHistogram::getAggregationTemporality, ExponentialHistogram.Builder::getAggregationTemporality));
    }

    @Override
    public ProtoExponentialHistogramAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        setValue(ExponentialHistogram.Builder::setAggregationTemporality, AggregationTemporality.toProto(aggregationTemporality));
        return this;
    }

}
