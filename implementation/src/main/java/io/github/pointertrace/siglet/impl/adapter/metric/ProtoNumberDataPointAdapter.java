package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.impl.adapter.Adapter;
import io.github.pointertrace.siglet.impl.adapter.AdapterConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.github.pointertrace.siglet.impl.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

import java.util.List;

public class ProtoNumberDataPointAdapter extends Adapter<NumberDataPoint, NumberDataPoint.Builder>
        implements io.github.pointertrace.siglet.api.signal.metric.NumberDataPoint {


    public ProtoNumberDataPointAdapter() {
        super(AdapterConfig.NUMBER_DATA_POINT_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
        addEnricher(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG, exemplars ->{
            getBuilder().clearExemplars();
            getBuilder().addAllExemplars((Iterable<Exemplar>) exemplars);
        });
    }

    protected List<KeyValue> getAttributeList() {
        return getValue(NumberDataPoint::getAttributesList, NumberDataPoint.Builder::getAttributesList);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        return  getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,this::getAttributeList);
    }

    @Override
    public long getStartTimeUnixNano() {
        return getValue(NumberDataPoint::getStartTimeUnixNano, NumberDataPoint.Builder::getStartTimeUnixNano);
    }

    @Override
    public long getTimeUnixNano() {
        return getValue(NumberDataPoint::getTimeUnixNano, NumberDataPoint.Builder::getTimeUnixNano);
    }

    @Override
    public ProtoNumberDataPointAdapter setAsLong(long value) {
        setValue(NumberDataPoint.Builder::setAsInt, value);
        return this;
    }

    @Override
    public ProtoNumberDataPointAdapter setAsDouble(double value) {
        setValue(NumberDataPoint.Builder::setAsDouble, value);
        return this;
    }

    @Override
    public int getFlags() {
        return getValue(NumberDataPoint::getFlags, NumberDataPoint.Builder::getFlags);
    }

    @Override
    public boolean hasDoubleValue() {
        return getValue(NumberDataPoint::hasAsDouble, NumberDataPoint.Builder::hasAsDouble);
    }

    @Override
    public boolean hasLongValue() {
        return getValue(NumberDataPoint::hasAsInt, NumberDataPoint.Builder::hasAsInt);
    }

    protected List<Exemplar> getExemplarsList() {
        return getValue(NumberDataPoint::getExemplarsList, NumberDataPoint.Builder::getExemplarsList);
    }

    @Override
    public ProtoExemplarsAdapter getExemplars() {
        return  getAdapterList(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG, this::getExemplarsList);
    }

    @Override
    public ProtoNumberDataPointAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        setValue(NumberDataPoint.Builder::setStartTimeUnixNano, startTimeUnixNano);
        return this;
    }

    @Override
    public ProtoNumberDataPointAdapter setTimeUnixNano(long timeUnixNano) {
        setValue(NumberDataPoint.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    @Override
    public long getAsLong() {
        return getValue(NumberDataPoint::getAsInt, NumberDataPoint.Builder::getAsInt);
    }

    @Override
    public double getAsDouble() {
        return getValue(NumberDataPoint::getAsDouble, NumberDataPoint.Builder::getAsDouble);
    }

    @Override
    public Object getValue() {
        if (hasDoubleValue()) {
            return getAsDouble();
        } else if (hasLongValue()) {
            return getAsLong();
        } else {
            return null;
        }
    }

    @Override
    public ProtoNumberDataPointAdapter setFlags(int flags) {
        setValue(NumberDataPoint.Builder::setFlags, flags);
        return this;
    }

}
