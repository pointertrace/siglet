package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableSummaryDataPoint;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

import java.util.List;

public class ProtoSummaryDataPointAdapter extends Adapter<SummaryDataPoint, SummaryDataPoint.Builder>
        implements ModifiableSummaryDataPoint {

    public ProtoSummaryDataPointAdapter() {
        super(AdapterConfig.SUMMARY_DATA_POINT_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
        addEnricher(AdapterListConfig.VALUE_AT_QUANTILE_ADAPTER_CONFIG, valueAtQuantiles -> {
            getBuilder().clearQuantileValues();
            getBuilder().addAllQuantileValues((Iterable<SummaryDataPoint.ValueAtQuantile>) valueAtQuantiles);
        });
    }

    protected List<KeyValue> getAttributeList() {
        return getValue(SummaryDataPoint::getAttributesList, SummaryDataPoint.Builder::getAttributesList);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        return  getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, this::getAttributeList);
    }

    public List<SummaryDataPoint.ValueAtQuantile> getQuantileValuesList() {
        return getValue(SummaryDataPoint::getQuantileValuesList, SummaryDataPoint.Builder::getQuantileValuesList);
    }

    @Override
    public ProtoValueAtQuantilesAdapter getQuantileValues() {
        return  getAdapterList(AdapterListConfig.VALUE_AT_QUANTILE_ADAPTER_CONFIG,
                this::getQuantileValuesList);
    }

    @Override
    public long getStartTimeUnixNano() {
        return getValue(SummaryDataPoint::getStartTimeUnixNano, SummaryDataPoint.Builder::getStartTimeUnixNano);
    }

    @Override
    public long getTimeUnixNano() {
        return getValue(SummaryDataPoint::getTimeUnixNano, SummaryDataPoint.Builder::getTimeUnixNano);
    }

    @Override
    public long getCount() {
        return getValue(SummaryDataPoint::getCount, SummaryDataPoint.Builder::getCount);
    }

    @Override
    public int getFlags() {
        return getValue(SummaryDataPoint::getFlags, SummaryDataPoint.Builder::getFlags);
    }

    @Override
    public double getSum() {
        return getValue(SummaryDataPoint::getSum, SummaryDataPoint.Builder::getSum);
    }

    @Override
    public ModifiableSummaryDataPoint setStartTimeUnixNano(long startTimeUnixNano) {
        setValue(SummaryDataPoint.Builder::setStartTimeUnixNano, startTimeUnixNano);
        return this;
    }

    @Override
    public ModifiableSummaryDataPoint setTimeUnixNano(long timeUnixNano) {
        setValue(SummaryDataPoint.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    @Override
    public ModifiableSummaryDataPoint setCount(long count) {
        setValue(SummaryDataPoint.Builder::setCount, count);
        return this;
    }

    @Override
    public ModifiableSummaryDataPoint setFlags(int flags) {
        setValue(SummaryDataPoint.Builder::setFlags, flags);
        return this;
    }

    @Override
    public ModifiableSummaryDataPoint setSum(double sum) {
        setValue(SummaryDataPoint.Builder::setSum, sum);
        return this;
    }

}
