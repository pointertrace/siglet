package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.Exemplars;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ExemplarsAdapter implements Exemplars {
    private final List<io.opentelemetry.proto.metrics.v1.Exemplar> exemplars;
    private final Consumer<List<io.opentelemetry.proto.metrics.v1.Exemplar>> onChange;

    public ExemplarsAdapter(List<io.opentelemetry.proto.metrics.v1.Exemplar> exemplars) {
        this(exemplars, null);
    }

    public ExemplarsAdapter(List<io.opentelemetry.proto.metrics.v1.Exemplar> exemplars,
                            Consumer<List<io.opentelemetry.proto.metrics.v1.Exemplar>> onChange) {
        this.exemplars = new ArrayList<>(Objects.requireNonNull(exemplars, "exemplars"));
        this.onChange = onChange;
    }

    public List<io.opentelemetry.proto.metrics.v1.Exemplar> getUpdated() {
        return new ArrayList<>(exemplars);
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(new ArrayList<>(exemplars));
        }
    }

    @Override
    public int getSize() {
        return exemplars.size();
    }

    @Override
    public ExemplarAdapter get(int i) {
        io.opentelemetry.proto.metrics.v1.Exemplar value = exemplars.get(i);
        return new ExemplarAdapter(value, updated -> {
            exemplars.set(i, updated);
            changed();
        });
    }

    @Override
    public void remove(int i) {
        exemplars.remove(i);
        changed();
    }
}
