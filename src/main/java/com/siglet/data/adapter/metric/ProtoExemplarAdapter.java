package com.siglet.data.adapter.metric;

import com.google.protobuf.ByteString;
import com.siglet.SigletError;
import com.siglet.data.adapter.Adapter;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.modifiable.metric.ModifiableExemplar;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoExemplarAdapter extends Adapter<Exemplar, Exemplar.Builder> implements ModifiableExemplar {


    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoExemplarAdapter(Exemplar protoExemplar, boolean updatable) {
        super(protoExemplar, Exemplar::toBuilder, Exemplar.Builder::build, updatable);
    }

    public ProtoExemplarAdapter(Exemplar.Builder protoExamplarBuilder) {
        super(protoExamplarBuilder, Exemplar.Builder::build);

    }


    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(getValue(Exemplar::getFilteredAttributesList,
                    Exemplar.Builder::getFilteredAttributesList), isUpdatable());
        }

        return protoAttributesAdapter;
    }

    @Override
    public long getTimeUnixNanos() {
        return getValue(Exemplar::getTimeUnixNano, Exemplar.Builder::getTimeUnixNano);
    }

    @Override
    public long getAsLong() {
        return getValue(Exemplar::getAsInt, Exemplar.Builder::getAsInt);
    }

    @Override
    public double getAsDouble() {
        return getValue(Exemplar::getAsDouble, Exemplar.Builder::getAsDouble);
    }

    @Override
    public long getSpanId() {
        return AdapterUtils.spanId(getValue(Exemplar::getSpanId, Exemplar.Builder::getSpanId).toByteArray());
    }

    @Override
    public long getTraceIdHigh() {
        return AdapterUtils.traceIdHigh(getValue(Exemplar::getTraceId, Exemplar.Builder::getTraceId).toByteArray());
    }

    @Override
    public long getTraceIdLow() {
        return AdapterUtils.traceIdLow(getValue(Exemplar::getTraceId, Exemplar.Builder::getTraceId).toByteArray());
    }

    @Override
    public byte[] getTraceId() {
        return getValue(Exemplar::getTraceId, Exemplar.Builder::getTraceId).toByteArray();
    }

    @Override
    public ProtoExemplarAdapter setTimeUnixNanos(long timeUnixNanos) {
        setValue(Exemplar.Builder::setTimeUnixNano, timeUnixNanos);
        return this;
    }

    @Override
    public ProtoExemplarAdapter setAsLong(long value) {
        setValue(Exemplar.Builder::setAsInt, value);
        return this;
    }

    @Override
    public ProtoExemplarAdapter setAsDouble(double value) {
        setValue(Exemplar.Builder::setAsDouble, value);
        return this;
    }

    @Override
    public ProtoExemplarAdapter setSpanId(long spanId) {
        setValue(Exemplar.Builder::setSpanId, ByteString.copyFrom(AdapterUtils.spanId(spanId)));
        return this;
    }

    @Override
    public ProtoExemplarAdapter setTraceId(long traceIdHigh, long traceIdLow) {
        setValue(Exemplar.Builder::setTraceId, ByteString.copyFrom(AdapterUtils.traceId(traceIdHigh, traceIdLow)));
        return this;
    }

    @Override
    public ProtoExemplarAdapter setTraceId(byte[] traceId) {
        setValue(Exemplar.Builder::setSpanId, ByteString.copyFrom(traceId));
        return this;
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated());
    }

    @Override
    protected void enrich(Exemplar.Builder builder) {
        if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
            builder.clearFilteredAttributes();
            builder.addAllFilteredAttributes(protoAttributesAdapter.getUpdated());
        }
    }

}
