package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableExponentialHistogram;
import com.siglet.api.unmodifiable.metric.AggregationTemporality;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogram;

public class ProtoExponentialHistogramAdapter extends Adapter<ExponentialHistogram, ExponentialHistogram.Builder>
        implements ModifiableExponentialHistogram {

    private ProtoExponentialHistogramDataPointsAdapter protoExponentialHistogramDataPointsAdapter;

    public ProtoExponentialHistogramAdapter(ExponentialHistogram protoHistogram) {
        super(protoHistogram, ExponentialHistogram::toBuilder, ExponentialHistogram.Builder::build);
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
                    getValue(ExponentialHistogram::getDataPointsList, ExponentialHistogram.Builder::getDataPointsList));
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
        return AdapterUtils.valueOf(
                getValue(ExponentialHistogram::getAggregationTemporality, ExponentialHistogram.Builder::getAggregationTemporality));
    }

    @Override
    public ProtoExponentialHistogramAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        setValue(ExponentialHistogram.Builder::setAggregationTemporality, AdapterUtils.valueOf(aggregationTemporality));
        return this;
    }

}
