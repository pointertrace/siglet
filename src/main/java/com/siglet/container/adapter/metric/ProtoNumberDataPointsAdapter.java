package com.siglet.container.adapter.metric;

import com.siglet.api.signal.metric.NumberDataPoints;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

public class ProtoNumberDataPointsAdapter extends AdapterList<NumberDataPoint, NumberDataPoint.Builder,
    ProtoNumberDataPointAdapter> implements NumberDataPoints {


    public ProtoNumberDataPointsAdapter() {
        super(AdapterListConfig.NUMBER_DATA_POINTS_ADAPTER_CONFIG);
    }

}
