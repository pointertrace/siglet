package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableSummaryDataPoint;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

public class ProtoSummaryDataPointAdapter extends Adapter<SummaryDataPoint, SummaryDataPoint.Builder>
        implements ModifiableSummaryDataPoint {

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoValueAtQuantilesAdapter protoValueAtQuantilesAdapter;

    public ProtoSummaryDataPointAdapter(SummaryDataPoint protoSummaryDataPoint, boolean updatable) {
        super(protoSummaryDataPoint, SummaryDataPoint::toBuilder, SummaryDataPoint.Builder::build, updatable);
    }

    public ProtoSummaryDataPointAdapter(SummaryDataPoint.Builder protoSummaryDataPointBuilder) {
        super(protoSummaryDataPointBuilder, SummaryDataPoint.Builder::build);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(getMessage().getAttributesList(), isUpdatable());
        }
        return protoAttributesAdapter;
    }

    @Override
    public ProtoValueAtQuantilesAdapter getQuantileValues() {
        if (protoValueAtQuantilesAdapter == null) {
            protoValueAtQuantilesAdapter = new ProtoValueAtQuantilesAdapter(getMessage().getQuantileValuesList(),
                    isUpdatable());
        }
        return protoValueAtQuantilesAdapter;
    }

    @Override
    public long getStartTimeUnixNano() {
        return getValue(SummaryDataPoint::getStartTimeUnixNano, SummaryDataPoint.Builder::getStartTimeUnixNano);
    }

    @Override
    public long getTimeUnixNano() {
        return getValue(SummaryDataPoint::getTimeUnixNano, SummaryDataPoint.Builder::getTimeUnixNano);
    }

    @Override
    public long getCount() {
        return getValue(SummaryDataPoint::getCount, SummaryDataPoint.Builder::getCount);
    }

    @Override
    public int getFlags() {
        return getValue(SummaryDataPoint::getFlags, SummaryDataPoint.Builder::getFlags);
    }

    @Override
    public double getSum() {
        return getValue(SummaryDataPoint::getSum, SummaryDataPoint.Builder::getSum);
    }

    @Override
    public ModifiableSummaryDataPoint setStartTimeUnixNano(long startTimeUnixNano) {
        setValue(SummaryDataPoint.Builder::setStartTimeUnixNano, startTimeUnixNano);
        return this;
    }

    @Override
    public ModifiableSummaryDataPoint setTimeUnixNano(long timeUnixNano) {
        setValue(SummaryDataPoint.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    @Override
    public ModifiableSummaryDataPoint setCount(long count) {
        setValue(SummaryDataPoint.Builder::setCount, count);
        return this;
    }

    @Override
    public ModifiableSummaryDataPoint setFlags(int flags) {
        setValue(SummaryDataPoint.Builder::setFlags, flags);
        return this;
    }

    @Override
    public ModifiableSummaryDataPoint setSum(double sum) {
        setValue(SummaryDataPoint.Builder::setSum, sum);
        return this;
    }


    @Override
    public boolean isUpdated() {
        return super.isUpdated() ||
                (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) ||
                (protoValueAtQuantilesAdapter != null && protoValueAtQuantilesAdapter.isUpdated());
    }

    @Override
    protected void enrich(SummaryDataPoint.Builder builder) {
        if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
            builder.clearAttributes();
            builder.addAllAttributes(protoAttributesAdapter.getUpdated());
        }
        if (protoValueAtQuantilesAdapter!= null && protoValueAtQuantilesAdapter.isUpdated()) {
            builder.clearQuantileValues();
            builder.addAllQuantileValues(protoValueAtQuantilesAdapter.getUpdated());
        }
    }
}
