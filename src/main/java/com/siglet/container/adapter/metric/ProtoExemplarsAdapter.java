package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableExemplars;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.Exemplar;

public class ProtoExemplarsAdapter extends AdapterList<Exemplar, Exemplar.Builder, ProtoExemplarAdapter>
        implements ModifiableExemplars {

    public ProtoExemplarsAdapter() {
        super(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG);
    }

}
