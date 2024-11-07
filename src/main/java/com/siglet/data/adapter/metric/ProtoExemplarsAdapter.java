package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.AdapterList;
import com.siglet.data.modifiable.metric.ModifiableExemplars;
import io.opentelemetry.proto.metrics.v1.Exemplar;

import java.util.List;

public class ProtoExemplarsAdapter extends AdapterList<Exemplar, Exemplar.Builder, ProtoExemplarAdapter>
        implements ModifiableExemplars {

    public ProtoExemplarsAdapter(List<Exemplar> exemplars, boolean updatable) {
        super(exemplars, updatable);
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
    public ProtoExemplarAdapter add() {
        return super.add();
    }

    @Override
    protected ProtoExemplarAdapter createNewAdapter() {
        return new ProtoExemplarAdapter(Exemplar.newBuilder());
    }

    @Override
    protected ProtoExemplarAdapter createAdapter(int i) {
        return new ProtoExemplarAdapter(getMessage(i),isUpdatable());
    }
}
