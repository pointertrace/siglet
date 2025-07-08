package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableNumberDataPoints;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

public class ProtoNumberDataPointsAdapter extends AdapterList<NumberDataPoint, NumberDataPoint.Builder,
    ProtoNumberDataPointAdapter> implements ModifiableNumberDataPoints {


    public ProtoNumberDataPointsAdapter() {
        super(AdapterListConfig.NUMBER_DATA_POINTS_ADAPTER_CONFIG);
    }

}
