package io.github.pointertrace.siglet.api.signal.trace;

public interface Status {

    StatusCode getCode();

    Status setCode(StatusCode code);

    String getStatusMessage();

    Status setStatusMessage(String description);

}
