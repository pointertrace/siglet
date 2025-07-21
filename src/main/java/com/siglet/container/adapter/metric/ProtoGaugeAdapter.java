package com.siglet.container.adapter.metric;

import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

import java.util.List;

public class ProtoGaugeAdapter extends Adapter<Gauge, Gauge.Builder> implements com.siglet.api.signal.metric.Gauge {


    public ProtoGaugeAdapter() {
        super(AdapterConfig.GAUGE_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.NUMBER_DATA_POINTS_ADAPTER_CONFIG, dataPoints -> {
            getBuilder().clearDataPoints();
            getBuilder().addAllDataPoints((Iterable<NumberDataPoint>) dataPoints);
        });
    }

    public List<NumberDataPoint> getDataPointsList() {
        return getValue(Gauge::getDataPointsList, Gauge.Builder::getDataPointsList);
    }

    @Override
    public ProtoNumberDataPointsAdapter getDataPoints() {
        return getAdapterList(AdapterListConfig.NUMBER_DATA_POINTS_ADAPTER_CONFIG,
                this::getDataPointsList);
    }

}
