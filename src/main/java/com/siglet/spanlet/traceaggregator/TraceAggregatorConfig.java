package com.siglet.spanlet.traceaggregator;

public class TraceAggregatorConfig {

    private Long timeoutMillis;

    private Long inactiveTimeoutMillis;

    private String completionExpression;

    public Long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(Number timeoutMillis) {
        this.timeoutMillis = timeoutMillis.longValue();
    }

    public Long getInactiveTimeoutMillis() {
        return inactiveTimeoutMillis;
    }

    public void setInactiveTimeoutMillis(Number inactiveTimeoutMillis) {
        this.inactiveTimeoutMillis = inactiveTimeoutMillis.longValue();
    }

    public String getCompletionExpression() {
        return completionExpression;
    }

    public void setCompletionExpression(String completionExpression) {
        this.completionExpression = completionExpression;
    }
}
