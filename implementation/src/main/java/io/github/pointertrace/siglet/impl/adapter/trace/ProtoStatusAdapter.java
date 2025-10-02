package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.trace.StatusCode;
import io.github.pointertrace.siglet.impl.adapter.Adapter;
import io.github.pointertrace.siglet.impl.adapter.AdapterConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.opentelemetry.proto.trace.v1.Status;

public class ProtoStatusAdapter extends Adapter<Status, Status.Builder> implements io.github.pointertrace.siglet.api.signal.trace.Status {


    public ProtoStatusAdapter() {
        super(AdapterConfig.STATUS_ADAPTER_CONFIG);
    }

    @Override
    public StatusCode getCode() {
        return AdapterUtils.getStatusCode(getValue(Status::getCode, Status.Builder::getCode));
    }

    @Override
    public String getStatusMessage() {
        return getValue(Status::getMessage, Status.Builder::getMessage);
    }

    @Override
    public ProtoStatusAdapter setCode(StatusCode code) {
        setValue(Status.Builder::setCode, AdapterUtils.getStatusCode(code));
        return this;
    }

    @Override
    public ProtoStatusAdapter setStatusMessage(String statusMessage) {
        setValue(Status.Builder::setMessage, statusMessage);
        return this;
    }
}
