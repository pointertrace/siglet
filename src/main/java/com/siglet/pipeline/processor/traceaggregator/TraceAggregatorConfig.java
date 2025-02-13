package com.siglet.pipeline.processor.traceaggregator;

import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;

public class TraceAggregatorConfig extends Item {

    private Long timeoutMillis;

    private Location timeoutMillisLocation;

    private Long inactiveTimeoutMillis;

    private Location inactiveTimeoutMillisLocation;

    private String completionExpression;

    private Location completionExpressionLocation;

    public Long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(Number timeoutMillis) {
        this.timeoutMillis = timeoutMillis.longValue();
    }

    public Location getTimeoutMillisLocation() {
        return timeoutMillisLocation;
    }

    public void setTimeoutMillisLocation(Location timeoutMillisLocation) {
        this.timeoutMillisLocation = timeoutMillisLocation;
    }

    public Long getInactiveTimeoutMillis() {
        return inactiveTimeoutMillis;
    }

    public void setInactiveTimeoutMillis(Number inactiveTimeoutMillis) {
        this.inactiveTimeoutMillis = inactiveTimeoutMillis.longValue();
    }

    public Location getInactiveTimeoutMillisLocation() {
        return inactiveTimeoutMillisLocation;
    }

    public void setInactiveTimeoutMillisLocation(Location inactiveTimeoutMillisLocation) {
        this.inactiveTimeoutMillisLocation = inactiveTimeoutMillisLocation;
    }
    public String getCompletionExpression() {
        return completionExpression;
    }

    public void setCompletionExpression(String completionExpression) {
        this.completionExpression = completionExpression;
    }

    public Location getCompletionExpressionLocation() {
        return completionExpressionLocation;
    }

    public void setCompletionExpressionLocation(Location completionExpressionLocation) {
        this.completionExpressionLocation = completionExpressionLocation;
    }


}
