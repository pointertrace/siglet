package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.Exemplars;
import io.github.pointertrace.siglet.impl.adapter.AdapterList;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.Exemplar;

public class ProtoExemplarsAdapter extends AdapterList<Exemplar, Exemplar.Builder, ProtoExemplarAdapter>
        implements Exemplars {

    public ProtoExemplarsAdapter() {
        super(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG);
    }

}
