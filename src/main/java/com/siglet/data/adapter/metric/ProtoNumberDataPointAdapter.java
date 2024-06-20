package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import com.siglet.data.adapter.ProtoAttributesAdapter;
import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.modifiable.metric.ModifiableNumberDataPoint;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoNumberDataPointAdapter implements ModifiableNumberDataPoint {

    private NumberDataPoint protoNumberDataPoint;

    private final boolean updatable;

    private boolean updated;

    private NumberDataPoint.Builder protoNumberDataPointBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoExemplarsAdapter protoExemplarsAdapter;

    public ProtoNumberDataPointAdapter(NumberDataPoint protoNumberDataPoint, boolean updatable) {
        this.protoNumberDataPoint = protoNumberDataPoint;
        this.updatable = updatable;
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoNumberDataPoint.getAttributesList(), updatable);
        }
        return protoAttributesAdapter;
    }

    @Override
    public ProtoExemplarsAdapter getExemplars() {
        if (protoExemplarsAdapter == null) {
            protoExemplarsAdapter = new ProtoExemplarsAdapter(protoNumberDataPoint.getExemplarsList(), updatable);
        }
        return protoExemplarsAdapter;
    }

    @Override
    public long getStartTimeUnixNano() {
        return protoNumberDataPointBuilder == null ? protoNumberDataPoint.getStartTimeUnixNano()
                : protoNumberDataPointBuilder.getStartTimeUnixNano();
    }

    @Override
    public void setStartTimeUnixNano(long startTimeUnixNano) {
        checkAndPrepareUpdate();
        protoNumberDataPointBuilder.setStartTimeUnixNano(startTimeUnixNano);
    }

    @Override
    public long getTimeUnixNano() {
        return protoNumberDataPointBuilder == null ? protoNumberDataPoint.getTimeUnixNano()
                : protoNumberDataPointBuilder.getTimeUnixNano();
    }

    @Override
    public void setTimeUnixNano(long timeUnixNano) {
        checkAndPrepareUpdate();
        protoNumberDataPointBuilder.setTimeUnixNano(timeUnixNano);
    }


    @Override
    public long getAsLong() {
        return protoNumberDataPointBuilder == null ? protoNumberDataPoint.getAsInt()
                : protoNumberDataPointBuilder.getAsInt();
    }

    @Override
    public void setAsLong(long value) {
        checkAndPrepareUpdate();
        protoNumberDataPointBuilder.setAsInt(value);
    }

    @Override
    public double getAsDouble() {
        return protoNumberDataPointBuilder == null ? protoNumberDataPoint.getAsDouble()
                : protoNumberDataPointBuilder.getAsDouble();
    }

    @Override
    public void setAsDouble(double value) {
        protoNumberDataPointBuilder.setAsDouble(value);
    }

    @Override
    public int getFlags() {
        return protoNumberDataPointBuilder == null ? protoNumberDataPoint.getFlags()
                : protoNumberDataPointBuilder.getFlags();
    }

    @Override
    public void setFlags(int flags) {
        checkAndPrepareUpdate();
        protoNumberDataPointBuilder.setFlags(flags);
    }

    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable span");
        }
        if (protoNumberDataPointBuilder == null) {
            protoNumberDataPointBuilder = protoNumberDataPoint.toBuilder();
        }
        updated = true;
    }

    private boolean attributesUpdated() {
        return protoAttributesAdapter != null && protoAttributesAdapter.isUpdated();
    }

    private boolean exemplarsUpdated() {
        return protoExemplarsAdapter != null && protoExemplarsAdapter.isUpdated();
    }

    public boolean isUpdated() {
        return updated || attributesUpdated() || exemplarsUpdated();
    }

    public NumberDataPoint getUpdatedNumberDataPointAdapter() {
        if (!updatable) {
            return protoNumberDataPoint;
        } else if (!updated && !attributesUpdated() && !exemplarsUpdated()) {
            return protoNumberDataPoint;
        } else {
            NumberDataPoint.Builder bld = protoNumberDataPointBuilder != null ?
                    protoNumberDataPointBuilder : protoNumberDataPoint.toBuilder();
            if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
                bld.clearAttributes();
                bld.addAllAttributes(protoAttributesAdapter.getAsKeyValueList());
            }
            if (protoExemplarsAdapter != null &&  protoExemplarsAdapter.isUpdated()) {
                bld.clearExemplars();
                bld.addAllExemplars(protoExemplarsAdapter.getUpdated());
            }
            return bld.build();

        }
    }
}
