package com.siglet.data.adapter.trace;

import com.google.protobuf.ByteString;
import com.siglet.data.adapter.ProtoAttributesAdapter;
import com.siglet.data.modifiable.trace.ModifiableLink;
import io.opentelemetry.proto.trace.v1.Span;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ProtoLinkAdapter implements ModifiableLink {

    private final Span.Link protoLink;

    private final boolean updatable;

    private boolean updated;

    private Span.Link.Builder protoLinkBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoLinkAdapter(Span.Link protoLink, boolean updatable) {
        this.protoLink = protoLink;
        this.updatable = updatable;
    }

    public long getTraceIdHigh() {
        return ByteBuffer.wrap(
                        Arrays.copyOfRange((protoLinkBuilder == null ?
                                protoLink.getTraceId() :
                                protoLinkBuilder.getTraceId()).toByteArray(), 0, 8))
                .getLong();
    }

    public long getTraceIdLow() {
        return ByteBuffer.wrap(
                        Arrays.copyOfRange((protoLinkBuilder == null ?
                                protoLink.getTraceId() :
                                protoLinkBuilder.getTraceId()).toByteArray(), 8, 16))
                .getLong();
    }

    public void setTraceId(long high, long low) {
        checkAndPrepareUpdate();
        protoLinkBuilder.setTraceId(ByteString.copyFrom(
                ByteBuffer.allocate(Long.BYTES * 2).putLong(high).putLong(low).array()));
    }

    public long getSpanId() {
        return ByteBuffer.wrap((protoLinkBuilder == null ?
                protoLink.getSpanId() :
                protoLinkBuilder.getSpanId()).toByteArray()).getLong();
    }


    public void setSpanId(long spanId) {
        checkAndPrepareUpdate();
        protoLinkBuilder.setSpanId(ByteString.copyFrom(ByteBuffer.allocate(Long.BYTES).putLong(spanId).array()));
    }

    public String getTraceState() {
        return protoLinkBuilder == null ? protoLink.getTraceState() : protoLinkBuilder.getTraceState();
    }

    public void setTraceState(String traceState) {
        checkAndPrepareUpdate();
        protoLinkBuilder.setTraceState(traceState);
    }

    public int getFlags() {
        return protoLinkBuilder == null ? protoLink.getFlags() : protoLinkBuilder.getFlags();
    }

    public void setFlags(int flags) {
        checkAndPrepareUpdate();
        protoLinkBuilder.setFlags(flags);
    }

    public int getDroppedAttributesCount() {
        return protoLinkBuilder == null ? protoLink.getDroppedAttributesCount() : protoLinkBuilder.getDroppedAttributesCount();
    }

    public void setDroppedAttributesCount(int droppedAttributesCount) {
        checkAndPrepareUpdate();
        protoLinkBuilder.setDroppedAttributesCount(droppedAttributesCount);
    }

    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoLink.getAttributesList(), updatable);
        }
        return protoAttributesAdapter;
    }

    public boolean isUpdated() {
        return updated;
    }

    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new IllegalStateException("trying to change a non updatable link!");
        }
        if (protoLinkBuilder == null) {
            protoLinkBuilder = protoLink.toBuilder();
        }
        updated = true;
    }


    public Span.Link getUpdated() {
        if (!updatable) {
            return protoLink;
        } else if (!updated && (protoAttributesAdapter == null || !protoAttributesAdapter.isUpdated())) {
            return protoLink;
        } else {
            Span.Link.Builder bld = protoLinkBuilder != null ?
                    protoLinkBuilder: protoLink.toBuilder();
            if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
                bld.clearAttributes();
                bld.addAllAttributes(protoAttributesAdapter.getAsKeyValueList());
            }
            return bld.build();
        }
    }
}
