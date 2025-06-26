package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableNumberDataPoint;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

import java.util.ArrayList;
import java.util.List;

public class ProtoNumberDataPointAdapter extends Adapter<NumberDataPoint, NumberDataPoint.Builder>
        implements ModifiableNumberDataPoint {

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoExemplarsAdapter protoExemplarsAdapter;

    public ProtoNumberDataPointAdapter(NumberDataPoint protoNumberDataPoint) {
        super(protoNumberDataPoint, NumberDataPoint::toBuilder, NumberDataPoint.Builder::build);
    }


    public ProtoNumberDataPointAdapter() {
        super(NumberDataPoint.newBuilder(), NumberDataPoint.Builder::build);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            List<KeyValue> attributes;
            if (getMessage() == null) {
                attributes = new ArrayList<>();
            } else if (getMessage().getAttributesList() == null) {
                attributes = new ArrayList<>();
            } else {
                attributes = getMessage().getAttributesList();
            }
            protoAttributesAdapter = new ProtoAttributesAdapter(attributes);
        }
        return protoAttributesAdapter;
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

    @Override
    public ProtoExemplarsAdapter getExemplars() {
        if (protoExemplarsAdapter == null) {
            protoExemplarsAdapter = new ProtoExemplarsAdapter(getMessage().getExemplarsList());

        }
        return protoExemplarsAdapter;
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

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) ||
                (protoExemplarsAdapter != null && protoExemplarsAdapter.isUpdated());
    }

    @Override
    protected void enrich(NumberDataPoint.Builder builder) {
        if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
            builder.clearAttributes();
            builder.addAllAttributes(protoAttributesAdapter.getUpdated());
        }
        if (protoExemplarsAdapter != null && protoExemplarsAdapter.isUpdated()) {
            builder.clearExemplars();
            builder.addAllExemplars(protoExemplarsAdapter.getUpdated());
        }
    }
}
