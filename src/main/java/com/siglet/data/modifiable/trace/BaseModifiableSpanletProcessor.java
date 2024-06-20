package com.siglet.data.modifiable.trace;

import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class BaseModifiableSpanletProcessor<T> implements Processor {

    private final ModifiableSpanletProcessor<T> spanLet;

    private final T config;

    public BaseModifiableSpanletProcessor(ModifiableSpanletProcessor<T> spanLet, T config) {
        this.spanLet = spanLet;
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        ProtoSpanAdapter spanAdapter = exchange.getIn().getBody(ProtoSpanAdapter.class);

        spanLet.span(spanAdapter,config);


    }
}
