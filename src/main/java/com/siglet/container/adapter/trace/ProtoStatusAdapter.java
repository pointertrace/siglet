package com.siglet.container.adapter.trace;

import com.siglet.api.signal.trace.StatusCode;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.trace.v1.Status;

public class ProtoStatusAdapter extends Adapter<Status, Status.Builder> implements com.siglet.api.signal.trace.Status {


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
