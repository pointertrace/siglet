package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.modifiable.metric.ModifiableSummary;
import io.opentelemetry.proto.metrics.v1.Summary;

public class ProtoSummaryAdapter extends Adapter<Summary, Summary.Builder> implements ModifiableSummary {

    private ProtoSummaryDataPointsAdapter protoSummaryDataPointsAdapter;

    public ProtoSummaryAdapter(Summary protoSummary, boolean updatable) {
        super(protoSummary,Summary::toBuilder,Summary.Builder::build,updatable);
    }

    public ProtoSummaryAdapter(Summary.Builder summaryBuilder) {
        super(summaryBuilder, Summary.Builder::build);
    }

    @Override
    public ProtoSummaryDataPointsAdapter getDataPoints() {
        if (protoSummaryDataPointsAdapter == null) {
            protoSummaryDataPointsAdapter = new ProtoSummaryDataPointsAdapter(
                    getValue(Summary::getDataPointsList,Summary.Builder::getDataPointsList),isUpdatable());
        }
        return protoSummaryDataPointsAdapter;
    }

    public boolean isUpdated() {
        return super.isUpdated() || (protoSummaryDataPointsAdapter != null && protoSummaryDataPointsAdapter.isUpdated());
    }

    @Override
    protected void enrich(Summary.Builder builder) {
        if (protoSummaryDataPointsAdapter != null && protoSummaryDataPointsAdapter.isUpdated()) {
            builder.clearDataPoints();
            builder.addAllDataPoints(protoSummaryDataPointsAdapter.getUpdated());
        }
    }
}
