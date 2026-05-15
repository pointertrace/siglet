package io.github.pointertrace.siglet.impl.adapter;

import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.Resource;

import java.util.Objects;

public final class ResourceAdapter implements Resource {
    private final io.opentelemetry.proto.resource.v1.Resource original;
    private io.opentelemetry.proto.resource.v1.Resource.Builder builder;
    private AttributesAdapter attributesAdapter;

    public ResourceAdapter(io.opentelemetry.proto.resource.v1.Resource resource) {
        this.original = Objects.requireNonNull(resource, "resource");
    }

    private io.opentelemetry.proto.resource.v1.Resource.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.resource.v1.ResourceOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.resource.v1.Resource getUpdated() {
        return builder == null ? original : builder.build();
    }

    @Override
    public int getDroppedAttributesCount() {
        return view().getDroppedAttributesCount();
    }

    @Override
    public ResourceAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        mutate().setDroppedAttributesCount(droppedAttributesCount);
        return this;
    }

    @Override
    public Attributes getAttributes() {
        if (attributesAdapter == null) {
            attributesAdapter = new AttributesAdapter(view().getAttributesList(), values -> {
                io.opentelemetry.proto.resource.v1.Resource.Builder b = mutate();
                b.clearAttributes();
                b.addAllAttributes(values);
            });
        }
        return attributesAdapter;
    }
}
