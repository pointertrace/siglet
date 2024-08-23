package com.siglet.data.adapter.metric;

import com.google.protobuf.Message;
import com.siglet.SigletError;
import com.siglet.data.Clonable;
import com.siglet.data.adapter.Adapter;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.adapter.common.ProtoInstrumentationScopeAdapter;
import com.siglet.data.adapter.common.ProtoResourceAdapter;
import com.siglet.data.modifiable.metric.ModifiableData;
import com.siglet.data.modifiable.metric.ModifiableMetric;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoMetricAdapter extends Adapter<Metric,Metric.Builder> implements ModifiableMetric, Clonable {


    private Resource protoResource;

    private InstrumentationScope protoInstrumentationScope;

    private ProtoResourceAdapter protoResourceAdapter;

    private ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;

    private ProtoGaugeAdapter protoGaugeAdapter;

    private ProtoSumAdapter protoSumAdapter;


    public ProtoMetricAdapter(Metric protoMetric, Resource protoResourceAdapter,
                              InstrumentationScope protoInstrumentationScopeAdapter, boolean updatable) {
        super(protoMetric,Metric::toBuilder,Metric.Builder::build,updatable);
        this.protoResource = protoResourceAdapter;
        this.protoInstrumentationScope = protoInstrumentationScopeAdapter;
    }

    @Override
    public String getName() {
        return getValue(Metric::getName,Metric.Builder::getName);
    }

    @Override
    public ProtoMetricAdapter setName(String name) {
        setValue(Metric.Builder::setName, name);
        return this;
    }


    @Override
    public String getDescription() {
        return getValue(Metric::getDescription,Metric.Builder::getDescription);
    }

    @Override
    public ProtoMetricAdapter setDescription(String description) {
        setValue(Metric.Builder::setDescription,description);
        return this;
    }

    @Override
    public String getUnit() {
        return getValue(Metric::getUnit,Metric.Builder::getUnit);
    }

    @Override
    public ProtoMetricAdapter setUnit(String unit) {
        setValue(Metric.Builder::setUnit, unit);
        return this;
    }

    @Override
    public ModifiableData getData() {
        if (hasGauge()) {
            if (protoGaugeAdapter == null) {
                protoGaugeAdapter = new ProtoGaugeAdapter(getMessage().getGauge(), isUpdatable());
            }
            return protoGaugeAdapter;
        } else if (hasSum()) {
            if (protoSumAdapter == null){
                protoSumAdapter = new ProtoSumAdapter(getMessage().getSum(),isUpdatable());
            }
            return protoSumAdapter;
        }
        throw new IllegalStateException("not implemented!");
    }

    @Override
    public boolean hasGauge() {
        return getValue(Metric::hasGauge,Metric.Builder::hasGauge);
    }

    @Override
    public ProtoGaugeAdapter getGauge() {
        if (! hasGauge()) {
            throw new SigletError("data is not a gauge ");
        } else {
            return (ProtoGaugeAdapter) getData();
        }
    }

    public boolean hasSum(){
        return getValue(Metric::hasSum,Metric.Builder::hasSum);
    }

    public ProtoSumAdapter getSum(){
        if (! hasGauge()) {
            throw new SigletError("data is not a sum ");
        } else {
            return (ProtoSumAdapter)  getData();
        }
    }

    public ProtoResourceAdapter getProtoResourceAdapter() {
        if (protoResourceAdapter != null){
            protoResourceAdapter = new ProtoResourceAdapter(protoResource,isUpdatable());
        }
        return protoResourceAdapter;
    }

    public ProtoInstrumentationScopeAdapter getProtoInstrumentationScopeAdapter() {
        if (protoInstrumentationScopeAdapter != null) {
            protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(protoInstrumentationScope,
            isUpdatable());
        }
        return protoInstrumentationScopeAdapter;
    }

    @Override
    public Object clone() {
        return null;
    }
}
