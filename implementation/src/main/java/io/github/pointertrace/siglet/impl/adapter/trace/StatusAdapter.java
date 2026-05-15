package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.trace.Status;
import io.github.pointertrace.siglet.api.signal.trace.StatusCode;
import io.github.pointertrace.siglet.impl.adapter.ProtoUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class StatusAdapter implements Status {
    private final io.opentelemetry.proto.trace.v1.Status original;
    private io.opentelemetry.proto.trace.v1.Status.Builder builder;
    private final Consumer<io.opentelemetry.proto.trace.v1.Status> onChange;

    public StatusAdapter(io.opentelemetry.proto.trace.v1.Status status) {
        this(status, null);
    }

    public StatusAdapter(io.opentelemetry.proto.trace.v1.Status status,
                         Consumer<io.opentelemetry.proto.trace.v1.Status> onChange) {
        this.original = Objects.requireNonNull(status, "status");
        this.onChange = onChange;
    }


    private io.opentelemetry.proto.trace.v1.Status.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.trace.v1.StatusOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.trace.v1.Status getUpdated() {
        return builder == null ? original : builder.build();
    }


    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public StatusCode getCode() {
        return ProtoUtil.fromProto(view().getCode());
    }

    @Override
    public StatusAdapter setCode(StatusCode code) {
        mutate().setCode(ProtoUtil.toProto(Objects.requireNonNull(code, "code")));
        changed();
        return this;
    }

    @Override
    public String getStatusMessage() {
        return view().getMessage();
    }

    @Override
    public StatusAdapter setStatusMessage(String statusMessage) {
        mutate().setMessage(Objects.requireNonNull(statusMessage, "statusMessage"));
        changed();
        return this;
    }
}
