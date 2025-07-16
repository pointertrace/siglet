package com.siglet.container.adapter.metric;

import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;

import java.util.ArrayList;
import java.util.List;

public class ProtoHistogramDataPointAdapter extends Adapter<HistogramDataPoint, HistogramDataPoint.Builder>
        implements com.siglet.api.data.metric.HistogramDataPoint {

    public ProtoHistogramDataPointAdapter() {
        super(AdapterConfig.HISTOGRAM_DATA_POINT_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
        addEnricher(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG, exemplars -> {
            getBuilder().clearExemplars();
            getBuilder().addAllExemplars((Iterable<Exemplar>) exemplars);
        });
    }

    public List<KeyValue> getAttributeList() {
       return getValue(HistogramDataPoint::getAttributesList, HistogramDataPoint.Builder::getAttributesList);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        return getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,this::getAttributeList);
    }

    @Override
    public long getStartTimeUnixNano() {
        return getValue(HistogramDataPoint::getStartTimeUnixNano, HistogramDataPoint.Builder::getStartTimeUnixNano);
    }

    @Override
    public long getTimeUnixNano() {
        return getValue(HistogramDataPoint::getTimeUnixNano, HistogramDataPoint.Builder::getTimeUnixNano);
    }

    @Override
    public long getCount() {
        return getValue(HistogramDataPoint::getCount, HistogramDataPoint.Builder::getCount);
    }


    @Override
    public int getFlags() {
        return getValue(HistogramDataPoint::getFlags, HistogramDataPoint.Builder::getFlags);
    }

    @Override
    public double getSum() {
        return getValue(HistogramDataPoint::getSum, HistogramDataPoint.Builder::getSum);
    }

    @Override
    public List<Long> getBucketCounts() {
        return new ArrayList<>(getValue(HistogramDataPoint::getBucketCountsList,
                HistogramDataPoint.Builder::getBucketCountsList));
    }

    @Override
    public List<Double> getExplicitBounds() {
        return getValue(HistogramDataPoint::getExplicitBoundsList, HistogramDataPoint.Builder::getExplicitBoundsList);
    }

    @Override
    public Double getMin() {
        return getValue(HistogramDataPoint::getMin, HistogramDataPoint.Builder::getMin);
    }

    @Override
    public Double getMax() {
        return getValue(HistogramDataPoint::getMax, HistogramDataPoint.Builder::getMax);
    }

    protected List<Exemplar> getExemplarsList() {
        return getValue(HistogramDataPoint::getExemplarsList,HistogramDataPoint.Builder::getExemplarsList);
    }

    @Override
    public ProtoExemplarsAdapter getExemplars() {
        return  getAdapterList(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG,this::getExemplarsList);
    }

    @Override
    public ProtoHistogramDataPointAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        setValue(HistogramDataPoint.Builder::setStartTimeUnixNano, startTimeUnixNano);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter setTimeUnixNano(long timeUnixNano) {
        setValue(HistogramDataPoint.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter setCount(long count) {
        setValue(HistogramDataPoint.Builder::setCount, count);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter setFlags(int flags) {
        setValue(HistogramDataPoint.Builder::setFlags, flags);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter setSum(double sum) {
        setValue(HistogramDataPoint.Builder::setSum, sum);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter addBucketCount(long count) {
        setValue(HistogramDataPoint.Builder::addBucketCounts, count);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter addAllBucketCounts(List<Long> count) {
        setValue(HistogramDataPoint.Builder::addAllBucketCounts, count);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter clearBucketCounts() {
        prepareUpdate();
        getBuilder().clearBucketCounts();
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter addExplicitBound(Double explicitBound) {
        setValue(HistogramDataPoint.Builder::addExplicitBounds, explicitBound);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter addAllExplicitBounds(List<Double> explicityBounds) {
        setValue(HistogramDataPoint.Builder::addAllExplicitBounds, explicityBounds);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter clearExplicitBounds() {
        prepareUpdate();
        getBuilder().clearExplicitBounds();
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter setMin(Double min) {
        setValue(HistogramDataPoint.Builder::setMin, min);
        return this;
    }

    @Override
    public ProtoHistogramDataPointAdapter setMax(Double max) {
        setValue(HistogramDataPoint.Builder::setMax, max);
        return this;
    }

}
