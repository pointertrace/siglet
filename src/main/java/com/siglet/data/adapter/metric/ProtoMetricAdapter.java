package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import com.siglet.data.Clonable;
import com.siglet.data.adapter.ProtoAttributesAdapter;
import com.siglet.data.adapter.ProtoInstrumentationScopeAdapter;
import com.siglet.data.adapter.ProtoResourceAdapter;
import com.siglet.data.adapter.metric.ProtoDataAdapter;
import com.siglet.data.modifiable.metric.ModifiableMetric;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoMetricAdapter implements ModifiableMetric, Clonable {

    private final Metric protoMetric;

    private final Resource protoResource;

    private final InstrumentationScope protoInstrumentationScope;

    private final boolean updatable;

    private boolean updated;

    private Metric.Builder protoMetricBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoResourceAdapter protoResourceAdapter;

    private ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;


    public ProtoMetricAdapter(Metric protoMetric, Resource protoResource,
                              InstrumentationScope protoInstrumentationScope, boolean updatable) {
        this.protoMetric = protoMetric;
        this.protoResource = protoResource;
        this.protoInstrumentationScope = protoInstrumentationScope;
        this.updatable = updatable;
    }

    @Override
    public String getName() {
        return protoMetricBuilder == null ? protoMetric.getName() : protoMetricBuilder.getName();
    }

    @Override
    public void setName(String name) {
        checkAndPrepareUpdate();
        protoMetricBuilder.setName(name);
    }


    @Override
    public String getDescription() {
        return protoMetricBuilder == null? protoMetric.getDescription() : protoMetricBuilder.getDescription();
    }

    @Override
    public void setDescription(String description) {
        checkAndPrepareUpdate();
        protoMetricBuilder.setDescription(description);
    }

    @Override
    public String getUnit() {
        return protoMetricBuilder == null ? protoMetric.getUnit() : protoMetricBuilder.getUnit();
    }

    @Override
    public void setUnit(String unit) {
        checkAndPrepareUpdate();
        protoMetricBuilder.setUnit(unit);
    }

    @Override
    public ProtoDataAdapter getData() {
        return null;
    }

    @Override
    public Object clone() {
        return null;
    }

    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable span");
        }
        if (protoMetricBuilder == null) {
            protoMetricBuilder = protoMetric.toBuilder();
        }
        updated = true;
    }
}
