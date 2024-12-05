package com.siglet.camel.component.otelgrpc.aggregator;

import com.siglet.SigletError;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class SignalAggregationStrategy implements AggregationStrategy {


    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            SignalsAggregator signalsAggregator = new SignalsAggregator();
            addToSignalsAggregator(signalsAggregator, newExchange.getIn().getBody());
            newExchange.getIn().setBody(signalsAggregator);
            return newExchange;
        } else {
            SignalsAggregator signalsAggregator = (SignalsAggregator) oldExchange.getIn().getBody();
            addToSignalsAggregator(signalsAggregator, newExchange.getIn().getBody());
            return oldExchange;
        }
    }

    private void addToSignalsAggregator(SignalsAggregator signalsAggregator, Object protoAdapter) {
        if (protoAdapter instanceof ProtoSpanAdapter protoSpanAdapter) {
            signalsAggregator.add(protoSpanAdapter);
        } else if (protoAdapter instanceof ProtoMetricAdapter protoMetricAdapter) {
            signalsAggregator.add(protoMetricAdapter);
        } else {
            throw new SigletError("Unsupported proto adapter type: " + protoAdapter.getClass().getName());
        }
    }
}
