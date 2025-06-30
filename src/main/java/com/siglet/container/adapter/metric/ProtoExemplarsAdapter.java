package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableExemplars;
import com.siglet.container.adapter.AdapterList;
import io.opentelemetry.proto.metrics.v1.Exemplar;

import java.util.List;

public class ProtoExemplarsAdapter extends AdapterList<Exemplar, Exemplar.Builder, ProtoExemplarAdapter>
        implements ModifiableExemplars {

    public ProtoExemplarsAdapter(List<Exemplar> exemplars) {
        super(exemplars);
    }

    public ProtoExemplarsAdapter() {
    }

    public ProtoExemplarsAdapter recycle(List<Exemplar> exemplars) {
        super.recycle(exemplars);
        return this;
    }

    @Override
    public ProtoExemplarAdapter get(int i) {
        return super.getAdapter(i);
    }

    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    protected ProtoExemplarAdapter createNewAdapter() {
        return new ProtoExemplarAdapter(Exemplar.newBuilder());
    }

    @Override
    protected ProtoExemplarAdapter createAdapter(int i) {
        return new ProtoExemplarAdapter(getMessage(i));
    }
}
