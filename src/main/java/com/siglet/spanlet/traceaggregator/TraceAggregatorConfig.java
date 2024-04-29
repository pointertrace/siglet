package com.siglet.spanlet.traceaggregator;

import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;

public class TraceAggregatorConfig extends Item {

    private ValueItem<Long> timeoutMillis;

    private ValueItem<Long> inactiveTimeoutMillis;

    private ValueItem<String> completionExpression;

    public ValueItem<Long> getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(ValueItem<Number> timeoutMillis) {
        this.timeoutMillis = new ValueItem<>(timeoutMillis.getLocation(), timeoutMillis.getValue().longValue());
    }

    public ValueItem<Long> getInactiveTimeoutMillis() {
        return inactiveTimeoutMillis;
    }

    public void setInactiveTimeoutMillis(ValueItem<Number> inactiveTimeoutMillis) {
        this.inactiveTimeoutMillis = new ValueItem<>(inactiveTimeoutMillis.getLocation(),
                inactiveTimeoutMillis.getValue().longValue());
    }

    public ValueItem<String> getCompletionExpression() {
        return completionExpression;
    }

    public void setCompletionExpression(ValueItem<String> completionExpression) {
        this.completionExpression = completionExpression;
    }
}
