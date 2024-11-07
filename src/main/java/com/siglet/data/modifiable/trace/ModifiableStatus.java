package com.siglet.data.modifiable.trace;

import com.siglet.data.trace.StatusCode;
import com.siglet.data.unmodifiable.trace.UnmodifiableStatus;

public interface ModifiableStatus extends UnmodifiableStatus {

    ModifiableStatus setCode(StatusCode code);

    ModifiableStatus setStatusMessage(String description);

}
