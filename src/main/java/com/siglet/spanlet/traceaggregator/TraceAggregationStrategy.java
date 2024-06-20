package com.siglet.spanlet.traceaggregator;

import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTraceAdapter;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class TraceAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            ProtoTraceAdapter protoTraceAdapter =
                    new ProtoTraceAdapter(newExchange.getIn().getBody(ProtoSpanAdapter.class), true);
            newExchange.getIn().setBody(protoTraceAdapter);
            return newExchange;
        } else {
            ProtoTraceAdapter protoTraceAdapter = oldExchange.getIn().getBody(ProtoTraceAdapter.class);
            protoTraceAdapter.add(newExchange.getIn().getBody(ProtoSpanAdapter.class));
            return oldExchange;
        }
    }
}
