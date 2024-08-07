package com.siglet.data.adapter.metric;

import com.google.protobuf.ByteString;
import com.siglet.SigletError;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.modifiable.metric.ModifiableExemplar;
import io.opentelemetry.proto.metrics.v1.Exemplar;

public class ProtoExemplarAdapter implements ModifiableExemplar {

    private final Exemplar protoExemplar;

    private final boolean updatable;

    private boolean updated;

    private Exemplar.Builder protoExamplerBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoExemplarAdapter(Exemplar protoExemplar, boolean updatable) {
        this.protoExemplar = protoExemplar;
        this.updatable = updatable;
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoExemplar.getFilteredAttributesList(), updatable);
        }

        return protoAttributesAdapter;
    }

    @Override
    public long getTimeUnixNanos() {
        return protoExamplerBuilder == null ?
                protoExemplar.getTimeUnixNano() : protoExamplerBuilder.getTimeUnixNano();
    }

    @Override
    public long getAsLong() {
        return protoExamplerBuilder == null ?
                protoExemplar.getAsInt(): protoExamplerBuilder.getAsInt();
    }

    @Override
    public double getAsDouble() {
        return protoExamplerBuilder == null ?
                protoExemplar.getAsDouble(): protoExamplerBuilder.getAsDouble();
    }

    @Override
    public long getSpanId() {
        return AdapterUtils.spanId(protoExamplerBuilder == null ?
                protoExemplar.getSpanId().toByteArray(): protoExamplerBuilder.getSpanId().toByteArray());
    }

    @Override
    public long getTraceIdHigh() {
        return AdapterUtils.traceIdHigh(protoExamplerBuilder == null ?
                protoExemplar.getTraceId().toByteArray(): protoExamplerBuilder.getTraceId().toByteArray());
    }

    @Override
    public long getTraceIdLow() {
        return AdapterUtils.traceIdLow(protoExamplerBuilder == null ?
                protoExemplar.getTraceId().toByteArray(): protoExamplerBuilder.getTraceId().toByteArray());
    }

    @Override
    public byte[] getTraceId() {
        return protoExamplerBuilder == null ?
                protoExemplar.getTraceId().toByteArray(): protoExamplerBuilder.getTraceId().toByteArray();
    }

    @Override
    public void setTimeUnixNanos(long timeUnixNanos) {
        checkAndPrepareUpdate();
        protoExamplerBuilder.setTimeUnixNano(timeUnixNanos);
    }

    @Override
    public void setAsLong(long value) {
        checkAndPrepareUpdate();
        protoExamplerBuilder.setAsInt(value);
    }

    @Override
    public void setAsDouble(double value) {
        checkAndPrepareUpdate();
        protoExamplerBuilder.setAsDouble(value);
    }

    @Override
    public void setSpanId(long spanId) {
        checkAndPrepareUpdate();
        protoExamplerBuilder.setSpanId(ByteString.copyFrom(AdapterUtils.spanId(spanId)));
    }

    @Override
    public void setTraceId(long traceIdHigh, long traceIdLow) {
        checkAndPrepareUpdate();
        protoExamplerBuilder.setTraceId(ByteString.copyFrom(AdapterUtils.traceId(traceIdHigh, traceIdLow)));
    }

    @Override
    public void setTraceId(byte[] traceId) {
        checkAndPrepareUpdate();
        protoExamplerBuilder.setTraceId(ByteString.copyFrom(traceId));
    }

    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable span");
        }
        if (protoExamplerBuilder == null) {
            protoExamplerBuilder = protoExemplar.toBuilder();
        }
        updated = true;
    }

    private boolean attributesUpdated() {
        return protoAttributesAdapter != null && protoAttributesAdapter.isUpdated();
    }

    public Exemplar getUpdatedExemplar() {
        if (!updatable) {
            return protoExemplar;
        } else if (!updated && !attributesUpdated()) {
            return protoExemplar;
        } else {
            Exemplar.Builder bld = protoExamplerBuilder != null ? protoExamplerBuilder : protoExemplar.toBuilder();
            if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
                bld.clearFilteredAttributes();
                bld.addAllFilteredAttributes(protoAttributesAdapter.getUpdated());
            }
            return bld.build();
        }
    }}
