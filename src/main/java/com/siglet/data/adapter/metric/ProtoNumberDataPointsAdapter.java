package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import com.siglet.data.modifiable.metric.ModifiableNumberDataPoint;
import com.siglet.data.modifiable.metric.ModifiableNumberDataPoints;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

import java.util.ArrayList;
import java.util.List;

public class ProtoNumberDataPointsAdapter implements ModifiableNumberDataPoints {


    private final List<NumberDataPoint> dataPoints;

    private final List<ProtoNumberDataPointAdapter> dataPointsAdapters;

    private final boolean updatable;

    private boolean updated;

    public ProtoNumberDataPointsAdapter(List<NumberDataPoint> dataPoints, boolean updatable) {
        // todo verificar se existe uma forma melhor para não criar objeto sem necessidade!
        this.dataPoints = new ArrayList<>(dataPoints);
        this.dataPointsAdapters = new ArrayList<>(dataPoints.size());
        for (int i = 0; i < dataPoints.size(); i++) {
            this.dataPointsAdapters.add(null);
        }
        this.updatable = updatable;
    }

    @Override
    public int getSize() {
        return dataPoints.size();
    }

    @Override
    public ProtoNumberDataPointAdapter getAt(int i) {
        if (dataPointsAdapters.get(i) == null) {
            dataPointsAdapters.set(i, new ProtoNumberDataPointAdapter(dataPoints.get(i), updatable));
        }
        return dataPointsAdapters.get(i);
    }

    @Override
    public void remove(int i) {
        checkUpdate();
        dataPoints.remove(i);
        dataPointsAdapters.remove(i);
    }

    public void add(ProtoNumberDataPointAdapter protoNumberDataPointAdapter) {
        checkUpdate();
        dataPoints.add(null);
        dataPointsAdapters.add(protoNumberDataPointAdapter);
    }

    public List<NumberDataPoint> getUpdated() {
        if (!updatable) {
            return dataPoints;
        } else if (!isUpdated()) {
            return dataPoints;
        } else {
            List<NumberDataPoint> result = new ArrayList<>();
            for (int i = 0; i < dataPointsAdapters.size(); i++) {
                result.add(dataPointsAdapters.get(i) == null ?
                        dataPoints.get(i) : dataPointsAdapters.get(i).getUpdatedNumberDataPointAdapter());
            }
            return result;
        }
    }

    public boolean isUpdated() {
        return updated;
    }

    private void checkUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable span");
        }
        updated = true;
    }
}
