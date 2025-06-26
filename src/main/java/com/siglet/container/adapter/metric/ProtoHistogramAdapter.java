package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableHistogram;
import com.siglet.api.unmodifiable.metric.AggregationTemporality;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.Histogram;

public class ProtoHistogramAdapter extends Adapter<Histogram, Histogram.Builder> implements ModifiableHistogram {

    private ProtoHistogramDataPointsAdapter protoHistogramDataPointsAdapter;

    public ProtoHistogramAdapter(Histogram protoHistogram) {
        super(protoHistogram, Histogram::toBuilder, Histogram.Builder::build);
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
                    getValue(Histogram::getDataPointsList, Histogram.Builder::getDataPointsList));
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
        return AdapterUtils.valueOf(
                getValue(Histogram::getAggregationTemporality, Histogram.Builder::getAggregationTemporality));
    }

    @Override
    public ProtoHistogramAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        setValue(Histogram.Builder::setAggregationTemporality, AdapterUtils.valueOf(aggregationTemporality));
        return this;
    }

}
