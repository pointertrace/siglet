package com.siglet.container.adapter.metric;

import com.siglet.api.data.metric.Exemplars;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.Exemplar;

public class ProtoExemplarsAdapter extends AdapterList<Exemplar, Exemplar.Builder, ProtoExemplarAdapter>
        implements Exemplars {

    public ProtoExemplarsAdapter() {
        super(AdapterListConfig.EXEMPLARS_ADAPTER_CONFIG);
    }

}
