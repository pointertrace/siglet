package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableHistogramDataPoint;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;

import java.util.ArrayList;
import java.util.List;

public class ProtoHistogramDataPointAdapter extends Adapter<HistogramDataPoint, HistogramDataPoint.Builder>
        implements ModifiableHistogramDataPoint {

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoExemplarsAdapter protoExemplarsAdapter;

    public ProtoHistogramDataPointAdapter(HistogramDataPoint protoHistogramDataPoint) {
        super(protoHistogramDataPoint, HistogramDataPoint::toBuilder, HistogramDataPoint.Builder::build);
    }


    public ProtoHistogramDataPointAdapter(HistogramDataPoint.Builder protoHistogramDataPointBuilder) {
        super(protoHistogramDataPointBuilder, HistogramDataPoint.Builder::build);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(getMessage().getAttributesList());
        }
        return protoAttributesAdapter;
    }

    @Override
    public long getStartTimeUnixNano() {
        return getValue(HistogramDataPoint::getStartTimeUnixNano, HistogramDataPoint.Builder::getStartTimeUnixNano);
    }

    @Override
    public long getTimeUnixNano() {
        return getValue(HistogramDataPoint::getTimeUnixNano, HistogramDataPoint.Builder::getTimeUnixNano);
    }

    @Override
    public long getCount() {
        return getValue(HistogramDataPoint::getCount, HistogramDataPoint.Builder::getCount);
    }


    @Override
    public int getFlags() {
        return getValue(HistogramDataPoint::getFlags, HistogramDataPoint.Builder::getFlags);
    }

    @Override
    public double getSum() {
        return getValue(HistogramDataPoint::getSum, HistogramDataPoint.Builder::getSum);
    }

    @Override
    public List<Long> getBucketCounts() {
        return new ArrayList<>(getValue(HistogramDataPoint::getBucketCountsList,
                HistogramDataPoint.Builder::getBucketCountsList));
    }

    @Override
    public List<Double> getExplicitBounds() {
        return getValue(HistogramDataPoint::getExplicitBoundsList, HistogramDataPoint.Builder::getExplicitBoundsList);
    }

    @Override
    public Double getMin() {
        return getValue(HistogramDataPoint::getMin, HistogramDataPoint.Builder::getMin);
    }

    @Override
    public Double getMax() {
        return getValue(HistogramDataPoint::getMax, HistogramDataPoint.Builder::getMax);
    }

    @Override
    public ProtoExemplarsAdapter getExemplars() {
        if (protoExemplarsAdapter == null) {
            protoExemplarsAdapter = new ProtoExemplarsAdapter(getMessage().getExemplarsList());

        }
        return protoExemplarsAdapter;
    }

    @Override
    public ProtoHistogramDataPointAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        setValue(HistogramDataPoint.Builder::setStartTimeUnixNano, startTimeUnixNano);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter setTimeUnixNano(long timeUnixNano) {
        setValue(HistogramDataPoint.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint setCount(long count) {
        setValue(HistogramDataPoint.Builder::setCount, count);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter setFlags(int flags) {
        setValue(HistogramDataPoint.Builder::setFlags, flags);
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint setSum(double sum) {
        setValue(HistogramDataPoint.Builder::setSum, sum);
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint addBucketCount(long count) {
        setValue(HistogramDataPoint.Builder::addBucketCounts, count);
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint addAllBucketCounts(List<Long> count) {
        setValue(HistogramDataPoint.Builder::addAllBucketCounts, count);
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint clearBucketCounts() {
        prepareUpdate();
        getBuilder().clearBucketCounts();
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint addExplicitBound(Double explicitBound) {
        setValue(HistogramDataPoint.Builder::addExplicitBounds, explicitBound);
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint addAllExplicitBounds(List<Double> explicityBounds) {
        setValue(HistogramDataPoint.Builder::addAllExplicitBounds, explicityBounds);
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint clearExplicitBounds() {
        prepareUpdate();
        getBuilder().clearExplicitBounds();
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint setMin(Double min) {
        setValue(HistogramDataPoint.Builder::setMin, min);
        return this;
    }

    @Override
    public ModifiableHistogramDataPoint setMax(Double max) {
        setValue(HistogramDataPoint.Builder::setMax, max);
        return this;
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) ||
                (protoExemplarsAdapter != null && protoExemplarsAdapter.isUpdated());
    }

    @Override
    protected void enrich(HistogramDataPoint.Builder builder) {
        if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
            builder.clearAttributes();
            builder.addAllAttributes(protoAttributesAdapter.getUpdated());
        }
        if (protoExemplarsAdapter != null && protoExemplarsAdapter.isUpdated()) {
            builder.clearExemplars();
            builder.addAllExemplars(protoExemplarsAdapter.getUpdated());
        }
    }
}
