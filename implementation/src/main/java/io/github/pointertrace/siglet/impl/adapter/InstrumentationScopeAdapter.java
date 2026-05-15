package io.github.pointertrace.siglet.impl.adapter;

import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.InstrumentationScope;

import java.util.Objects;

public final class InstrumentationScopeAdapter implements InstrumentationScope {
    private final io.opentelemetry.proto.common.v1.InstrumentationScope original;
    private io.opentelemetry.proto.common.v1.InstrumentationScope.Builder builder;
    private AttributesAdapter attributesAdapter;

    public InstrumentationScopeAdapter(io.opentelemetry.proto.common.v1.InstrumentationScope scope) {
        this.original = Objects.requireNonNull(scope, "scope");
    }

    private io.opentelemetry.proto.common.v1.InstrumentationScope.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.common.v1.InstrumentationScopeOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.common.v1.InstrumentationScope getUpdated() {
        return builder == null ? original : builder.build();
    }

    @Override
    public String getName() {
        return view().getName();
    }

    @Override
    public InstrumentationScopeAdapter setName(String name) {
        mutate().setName(Objects.requireNonNull(name, "name"));
        return this;
    }

    @Override
    public String getVersion() {
        return view().getVersion();
    }

    @Override
    public InstrumentationScopeAdapter setVersion(String version) {
        mutate().setVersion(Objects.requireNonNull(version, "version"));
        return this;
    }

    @Override
    public int getDroppedAttributesCount() {
        return view().getDroppedAttributesCount();
    }

    @Override
    public InstrumentationScopeAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        mutate().setDroppedAttributesCount(droppedAttributesCount);
        return this;
    }

    @Override
    public Attributes getAttributes() {
        if (attributesAdapter == null) {
            attributesAdapter = new AttributesAdapter(view().getAttributesList(), values -> {
                io.opentelemetry.proto.common.v1.InstrumentationScope.Builder b = mutate();
                b.clearAttributes();
                b.addAllAttributes(values);
            });
        }
        return attributesAdapter;
    }
}
