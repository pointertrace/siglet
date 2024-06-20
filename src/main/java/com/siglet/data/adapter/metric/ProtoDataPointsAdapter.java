package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import com.siglet.data.adapter.metric.ProtoNumberDataPointAdapter;
import com.siglet.data.modifiable.metric.ModifiableNumberDataPoint;
import com.siglet.data.modifiable.metric.ModifiableNumberDataPoints;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

import java.util.ArrayList;
import java.util.List;

public class ProtoDataPointsAdapter implements ModifiableNumberDataPoints {


    private final List<NumberDataPoint> dataPoints;

    private final List<ProtoNumberDataPointAdapter> dataPointsAdapter;

    private final boolean updatable;

    private boolean updated;

    public ProtoDataPointsAdapter(List<NumberDataPoint> dataPoints, boolean updatable) {
        this.dataPoints = dataPoints;
        this.dataPointsAdapter = new ArrayList<>(dataPoints.size());
        for (int i = 0; i < dataPoints.size(); i++) {
            this.dataPointsAdapter.add(null);
        }
        this.updatable = updatable;
    }

    @Override
    public int getSize() {
        return dataPoints.size();
    }

    @Override
    public ModifiableNumberDataPoint get(int i) {
        if (dataPointsAdapter.get(i) == null) {
//            dataPointsAdapter.set(i, new ProtoNumberDataPointAdapter(dataPoints.get(i), updatable));
        }
        return dataPointsAdapter.get(i);
    }

    @Override
    public void remove(int i) {
        checkAndPrepareUpdate();
        dataPoints.remove(i);
        dataPointsAdapter.remove(i);
    }


    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable span");
        }
        updated = true;
    }
}
