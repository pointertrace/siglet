package com.siglet.pipeline.spanlet.traceaggregator;

import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTrace;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class TraceAggregatorStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            ProtoTrace protoTraceAdapter =
                    new ProtoTrace(newExchange.getIn().getBody(ProtoSpanAdapter.class), true);
            newExchange.getIn().setBody(protoTraceAdapter);
            return newExchange;
        } else {
            ProtoTrace protoTraceAdapter = oldExchange.getIn().getBody(ProtoTrace.class);
            protoTraceAdapter.add(newExchange.getIn().getBody(ProtoSpanAdapter.class));
            return oldExchange;
        }
    }
}
