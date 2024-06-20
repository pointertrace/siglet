package com.siglet.data.unmodifiable.trace;

import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class BaseUnmodifiableSpanletProcessor<T> implements Processor {

    private final UnmodifiableSpanletProcessor spanlet;

    private final T config;


    public BaseUnmodifiableSpanletProcessor(UnmodifiableSpanletProcessor spanlet, T config) {
        this.spanlet = spanlet;
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        ProtoSpanAdapter spanAdapter = exchange.getIn().getBody(ProtoSpanAdapter.class);
        spanlet.span(spanAdapter, config);

    }
}
