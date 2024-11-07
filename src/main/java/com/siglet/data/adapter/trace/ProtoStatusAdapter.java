package com.siglet.data.adapter.trace;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.modifiable.trace.ModifiableStatus;
import com.siglet.data.trace.StatusCode;
import io.opentelemetry.proto.trace.v1.Status;

public class ProtoStatusAdapter extends Adapter<Status, Status.Builder> implements ModifiableStatus {

    public ProtoStatusAdapter(Status protoStatus, boolean updatable) {
        super(protoStatus, Status::toBuilder, Status.Builder::build, updatable);
    }

    public ProtoStatusAdapter() {
        super(Status.newBuilder(), Status.Builder::build);
    }

    @Override
    public StatusCode getCode() {
        return StatusCode.valueOf(getValue(Status::getCode, Status.Builder::getCode));
    }

    @Override
    public String getStatusMessage() {
        return getValue(Status::getMessage, Status.Builder::getMessage);
    }

    @Override
    public ProtoStatusAdapter setCode(StatusCode code) {
        setValue(Status.Builder::setCode, StatusCode.valueOf(code));
        return this;
    }

    @Override
    public ProtoStatusAdapter setStatusMessage(String statusMessage) {
        setValue(Status.Builder::setMessage, statusMessage);
        return this;
    }
}
