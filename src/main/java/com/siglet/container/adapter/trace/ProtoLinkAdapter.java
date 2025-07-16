package com.siglet.container.adapter.trace;

import com.google.protobuf.ByteString;
import com.siglet.api.data.trace.Link;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class ProtoLinkAdapter extends Adapter<Span.Link, Span.Link.Builder> implements Link {

    public ProtoLinkAdapter() {
        super(AdapterConfig.LINK_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
    }

    public long getTraceIdHigh() {
        return ByteBuffer.wrap(
                getValue(Span.Link::getTraceId, Span.Link.Builder::getTraceId).toByteArray(), 0, 8).getLong();
    }

    public long getTraceIdLow() {
        return ByteBuffer.wrap(
                        Arrays.copyOfRange(getValue(
                                Span.Link::getTraceId, Span.Link.Builder::getTraceId).toByteArray(), 8, 16))
                .getLong();
    }

    public ProtoLinkAdapter setTraceId(long high, long low) {
        setValue(Span.Link.Builder::setTraceId, ByteString.copyFrom(
                ByteBuffer.allocate(Long.BYTES * 2).putLong(high).putLong(low).array()));
        return this;
    }

    public long getSpanId() {
        return ByteBuffer.wrap(
                getValue(Span.Link::getSpanId, Span.Link.Builder::getSpanId).toByteArray()).getLong();
    }

    @Override
    public String getSpanIdEx() {
        return Long.toHexString(getSpanId());
    }

    @Override
    public String getTraceIdEx() {
        return Long.toHexString(getTraceIdHigh()) + Long.toHexString(getTraceIdLow());
    }

    public ProtoLinkAdapter setSpanId(long spanId) {
        setValue(Span.Link.Builder::setSpanId, ByteString.copyFrom(ByteBuffer.allocate(Long.BYTES).
                putLong(spanId).array()));
        return this;
    }

    public String getTraceState() {
        return getValue(Span.Link::getTraceState, Span.Link.Builder::getTraceState);
    }

    public Link setTraceState(String traceState) {
        setValue(Span.Link.Builder::setTraceState, traceState);
        return this;
    }

    public int getFlags() {
        return getValue(Span.Link::getFlags, Span.Link.Builder::getFlags);
    }

    public ProtoLinkAdapter setFlags(int flags) {
        setValue(Span.Link.Builder::setFlags, flags);
        return this;
    }

    public int getDroppedAttributesCount() {
        return getValue(Span.Link::getDroppedAttributesCount, Span.Link.Builder::getDroppedAttributesCount);
    }

    public ProtoLinkAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        setValue(Span.Link.Builder::setDroppedAttributesCount, droppedAttributesCount);
        return this;
    }

    protected List<KeyValue> getAttributeList() {
        return getValue(Span.Link::getAttributesList, Span.Link.Builder::getAttributesList);
    }

    public ProtoAttributesAdapter getAttributes() {
        return (ProtoAttributesAdapter) getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,
                this::getAttributeList);
    }

}
