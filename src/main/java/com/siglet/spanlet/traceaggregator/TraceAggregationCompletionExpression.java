package com.siglet.spanlet.traceaggregator;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class TraceAggregationCompletionExpression implements Predicate {
    @Override
    public boolean matches(Exchange exchange) {
        return false;
    }
}
