package com.siglet.pipeline.processor.traceaggregator;

import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;

import java.util.Base64;

public class TraceAggregatorCorrelationExpression implements Expression {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        ProtoSpanAdapter span = exchange.getIn().getBody(ProtoSpanAdapter.class);
        return type.cast(new String(ENCODER.encode(span.getTraceId())));
    }
}
