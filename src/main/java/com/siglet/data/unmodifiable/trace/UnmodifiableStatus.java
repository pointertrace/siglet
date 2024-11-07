package com.siglet.data.unmodifiable.trace;

import com.siglet.data.trace.StatusCode;

public interface UnmodifiableStatus {

    StatusCode getCode();

    String getStatusMessage();

}
