package com.siglet.data.trace;

import io.opentelemetry.proto.trace.v1.Status;

public enum StatusCode {

    UNSET(Status.StatusCode.STATUS_CODE_UNSET),
    OK(Status.StatusCode.STATUS_CODE_OK),
    ERROR(Status.StatusCode.STATUS_CODE_ERROR);

    private final Status.StatusCode protoStatusCode;

    StatusCode(Status.StatusCode protoStatusCode) {
        this.protoStatusCode = protoStatusCode;
    }

    public Status.StatusCode getProtoStatusCode() {
        return protoStatusCode;

    }

    public static StatusCode valueOf(Status.StatusCode statusCode) {
        return switch (statusCode) {
            case STATUS_CODE_OK -> OK;
            case STATUS_CODE_ERROR -> ERROR;
            default -> UNSET;
        };
    }

    public static Status.StatusCode valueOf(StatusCode statusCode) {
        return switch (statusCode) {
            case OK -> Status.StatusCode.STATUS_CODE_OK;
            case ERROR -> Status.StatusCode.STATUS_CODE_ERROR;
            default -> Status.StatusCode.STATUS_CODE_UNSET;
        };

    }

}
