package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.impl.adapter.Adapter;
import io.github.pointertrace.siglet.impl.adapter.AdapterConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.github.pointertrace.siglet.impl.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

import java.util.List;

public class ProtoExponentialHistogramDataPointAdapter extends Adapter<ExponentialHistogramDataPoint,
        ExponentialHistogramDataPoint.Builder> implements io.github.pointertrace.siglet.api.signal.metric.ExponentialHistogramDataPoint {

    public ProtoExponentialHistogramDataPointAdapter() {
        super(AdapterConfig.EXPONENTIAL_HISTOGRAM_DATAPOINT_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
        addEnricher(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG, exemplars -> {
            getBuilder().clearExemplars();
            getBuilder().addAllExemplars((Iterable<Exemplar>) exemplars);
        });
        addEnricher(AdapterConfig.POSITIVE_BUCKETS_ADAPTER_CONFIG, buckets ->
                getBuilder().setPositive((ExponentialHistogramDataPoint.Buckets) buckets)
        );
        addEnricher(AdapterConfig.NEGATIVE_BUCKETS_ADAPTER_CONFIG, buckets ->
                getBuilder().setNegative((ExponentialHistogramDataPoint.Buckets) buckets)
        );
    }


    protected List<KeyValue> getAttributeList() {
        return getValue(ExponentialHistogramDataPoint::getAttributesList,
                ExponentialHistogramDataPoint.Builder::getAttributesList);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        return getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,
                this::getAttributeList);
    }

    @Override
    public long getStartTimeUnixNano() {
        return getValue(ExponentialHistogramDataPoint::getStartTimeUnixNano,
                ExponentialHistogramDataPoint.Builder::getStartTimeUnixNano);
    }

    @Override
    public long getTimeUnixNano() {
        return getValue(ExponentialHistogramDataPoint::getTimeUnixNano,
                ExponentialHistogramDataPoint.Builder::getTimeUnixNano);
    }

    @Override
    public long getCount() {
        return getValue(ExponentialHistogramDataPoint::getCount, ExponentialHistogramDataPoint.Builder::getCount);
    }


    @Override
    public int getFlags() {
        return getValue(ExponentialHistogramDataPoint::getFlags, ExponentialHistogramDataPoint.Builder::getFlags);
    }

    @Override
    public double getSum() {
        return getValue(ExponentialHistogramDataPoint::getSum, ExponentialHistogramDataPoint.Builder::getSum);
    }

    @Override
    public int getScale() {
        return getValue(ExponentialHistogramDataPoint::getScale, ExponentialHistogramDataPoint.Builder::getScale);
    }

    @Override
    public long getZeroCount() {
        return getValue(ExponentialHistogramDataPoint::getZeroCount,
                ExponentialHistogramDataPoint.Builder::getZeroCount);
    }

    @Override
    public double getMax() {
        return getValue(ExponentialHistogramDataPoint::getMax, ExponentialHistogramDataPoint.Builder::getMax);
    }

    @Override
    public double getMin() {
        return getValue(ExponentialHistogramDataPoint::getMin, ExponentialHistogramDataPoint.Builder::getMin);
    }

    @Override
    public double getZeroThreshold() {
        return getValue(ExponentialHistogramDataPoint::getZeroThreshold,
                ExponentialHistogramDataPoint.Builder::getZeroThreshold);
    }

    protected ExponentialHistogramDataPoint.Buckets getPositiveBuckets() {
        return getValue(ExponentialHistogramDataPoint::getPositive,
                ExponentialHistogramDataPoint.Builder::getPositive);
    }

    @Override
    public ProtoBucketsAdapter getPositive() {
        return getAdapter(AdapterConfig.POSITIVE_BUCKETS_ADAPTER_CONFIG, this::getPositiveBuckets);
    }

    protected ExponentialHistogramDataPoint.Buckets getNegativeBuckets() {
        return getValue(ExponentialHistogramDataPoint::getNegative,
                ExponentialHistogramDataPoint.Builder::getNegative);
    }

    @Override
    public ProtoBucketsAdapter getNegative() {
        return getAdapter(AdapterConfig.NEGATIVE_BUCKETS_ADAPTER_CONFIG,
                this::getNegativeBuckets);
    }

    protected List<Exemplar> getExemplarsList() {
        return getValue(ExponentialHistogramDataPoint::getExemplarsList,
                ExponentialHistogramDataPoint.Builder::getExemplarsList);
    }

    @Override
    public ProtoExemplarsAdapter getExemplars() {
        return getAdapterList(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG,
                this::getExemplarsList);
    }

    @Override
   public ProtoExponentialHistogramDataPointAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        setValue(ExponentialHistogramDataPoint.Builder::setStartTimeUnixNano, startTimeUnixNano);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setTimeUnixNano(long timeUnixNano) {
        setValue(ExponentialHistogramDataPoint.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setCount(long count) {
        setValue(ExponentialHistogramDataPoint.Builder::setCount, count);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setFlags(int flags) {
        setValue(ExponentialHistogramDataPoint.Builder::setFlags, flags);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setSum(double sum) {
        setValue(ExponentialHistogramDataPoint.Builder::setSum, sum);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setScale(int scale) {
        setValue(ExponentialHistogramDataPoint.Builder::setScale, scale);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setZeroCount(long zeroCount) {
        setValue(ExponentialHistogramDataPoint.Builder::setZeroCount, zeroCount);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setMax(double max) {
        setValue(ExponentialHistogramDataPoint.Builder::setMax, max);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setMin(double min) {
        setValue(ExponentialHistogramDataPoint.Builder::setMin, min);
        return this;
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter setZeroThreshold(double zeroThreshold) {
        setValue(ExponentialHistogramDataPoint.Builder::setZeroThreshold, zeroThreshold);
        return this;
    }

}
