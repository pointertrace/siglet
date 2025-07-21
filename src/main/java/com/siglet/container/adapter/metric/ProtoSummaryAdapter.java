package com.siglet.container.adapter.metric;

import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.Summary;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

import java.util.List;

public class ProtoSummaryAdapter extends Adapter<Summary, Summary.Builder> implements com.siglet.api.signal.metric.Summary {

    public ProtoSummaryAdapter() {
        super(AdapterConfig.SUMMARY_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.SUMMARY_DATA_POINTS_ADAPTER_CONFIG, dataPoints -> {
            getBuilder().clearDataPoints();
            getBuilder().addAllDataPoints((Iterable<SummaryDataPoint>) dataPoints);
        });
    }

    public List<SummaryDataPoint> getDataPointsList() {
        return getValue(Summary::getDataPointsList, Summary.Builder::getDataPointsList);
    }

    @Override
    public ProtoSummaryDataPointsAdapter getDataPoints() {
        return getAdapterList(AdapterListConfig.SUMMARY_DATA_POINTS_ADAPTER_CONFIG, this::getDataPointsList);
    }

}
