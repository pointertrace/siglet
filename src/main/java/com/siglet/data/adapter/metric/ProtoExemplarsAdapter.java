package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import com.siglet.data.modifiable.metric.ModifiableExemplar;
import com.siglet.data.modifiable.metric.ModifiableExemplars;
import com.siglet.data.unmodifiable.UnmodifiableAttributes;
import io.opentelemetry.proto.metrics.v1.Exemplar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtoExemplarsAdapter implements ModifiableExemplars {

    private final List<Exemplar> exemplars;

    private final List<ProtoExemplarAdapter> exemplarsAdapters;

    private final boolean updatable;

    private boolean updated;

    public ProtoExemplarsAdapter(List<Exemplar> exemplars, boolean updatable) {
        this.exemplars = new ArrayList<>(exemplars);
        this.exemplarsAdapters = new ArrayList<>(exemplars.size());
        for(int i=0; i< exemplars.size(); i++) {
            this.exemplarsAdapters.add(null);
        }
        this.updatable = updatable;
    }

    @Override
    public int getSize() {
        return exemplars.size();
    }

    @Override
    public ProtoExemplarAdapter get(int i) {
        if (exemplarsAdapters.get(i) == null) {
            exemplarsAdapters.set(i, new ProtoExemplarAdapter(exemplars.get(i), updatable));
        }
        return exemplarsAdapters.get(i);
    }

    @Override
    public void remove(int i) {
        checkUpdate();
        exemplars.remove(i);
        exemplarsAdapters.remove(i);
    }

    public void add(ProtoExemplarAdapter exemplar) {
        checkUpdate();
        exemplars.add(null);
        exemplarsAdapters.add(exemplar);
    }

    public List<Exemplar> getUpdated() {
        if (!updatable) {
            return exemplars;
        } else if (! isUpdated()) {
            return exemplars;
        } else {
            List<Exemplar> result = new ArrayList<>(exemplars.size());
            for (int i = 0; i < exemplars.size(); i++) {
                result.add(exemplarsAdapters.get(i) == null ?
                        exemplars.get(i) : exemplarsAdapters.get(i).getUpdatedExemplar());
            }
            return result;
        }
    }

    public boolean isUpdated() {
        return updated;
    }

    private void checkUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable exemplars list!");
        }
        updated = true;
    }
}
