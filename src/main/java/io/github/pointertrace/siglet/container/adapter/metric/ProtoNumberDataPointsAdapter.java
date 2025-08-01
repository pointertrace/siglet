package io.github.pointertrace.siglet.container.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.NumberDataPoints;
import io.github.pointertrace.siglet.container.adapter.AdapterList;
import io.github.pointertrace.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

public class ProtoNumberDataPointsAdapter extends AdapterList<NumberDataPoint, NumberDataPoint.Builder,
    ProtoNumberDataPointAdapter> implements NumberDataPoints {


    public ProtoNumberDataPointsAdapter() {
        super(AdapterListConfig.NUMBER_DATA_POINTS_ADAPTER_CONFIG);
    }

}
