package com.siglet.container.adapter.metric;

import com.google.protobuf.ByteString;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Exemplar;

public class ProtoExemplarAdapter extends Adapter<Exemplar, Exemplar.Builder> implements com.siglet.api.data.metric.Exemplar {


    public ProtoExemplarAdapter() {
        super(AdapterConfig.EXEMPLAR_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,attributes -> {
            getBuilder().clearFilteredAttributes();
            getBuilder().addAllFilteredAttributes((Iterable<KeyValue>) attributes);
        });
    }


    @Override
    public ProtoAttributesAdapter getAttributes() {
        return getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,
                getMessage()::getFilteredAttributesList);
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
        setValue(Exemplar.Builder::setSpanId, AdapterUtils.spanId(spanId));
        return this;
    }

    @Override
    public ProtoExemplarAdapter setTraceId(long traceIdHigh, long traceIdLow) {
        setValue(Exemplar.Builder::setTraceId, AdapterUtils.traceId(traceIdHigh, traceIdLow));
        return this;
    }

    @Override
    public ProtoExemplarAdapter setTraceId(byte[] traceId) {
        setValue(Exemplar.Builder::setSpanId, ByteString.copyFrom(traceId));
        return this;
    }

}
