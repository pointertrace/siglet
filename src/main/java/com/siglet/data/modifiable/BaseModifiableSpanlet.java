package com.siglet.data.modifiable;

import com.siglet.data.adapter.ProtoSpanAdapter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public abstract class BaseModifiableSpanlet implements ModifiableSpanlet, Processor {
    @Override
    public abstract void span(ModifiableSpan span);

    @Override
    public void process(Exchange exchange) throws Exception {

        ProtoSpanAdapter spanAdapter = exchange.getIn(ProtoSpanAdapter.class);

        span(spanAdapter);

    }
}
