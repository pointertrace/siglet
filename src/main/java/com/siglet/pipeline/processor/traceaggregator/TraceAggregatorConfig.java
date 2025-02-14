package com.siglet.pipeline.processor.traceaggregator;

import com.siglet.config.item.Describable;
import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

public class TraceAggregatorConfig implements Located, Describable {

    private Location location;

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

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String describe(int level) {

        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe());
        sb.append("  traceAggregationConfig: ");
        sb.append(getTimeoutMillis());
        sb.append("\n");

        sb.append(prefix(level + 1));
        sb.append(getTimeoutMillisLocation().describe());
        sb.append("  timeout-millis: ");
        sb.append(getTimeoutMillis());
        sb.append("\n");

        sb.append(prefix(level + 1));
        sb.append(getInactiveTimeoutMillisLocation().describe());
        sb.append("  inactive-timeout-millis: ");
        sb.append(getInactiveTimeoutMillis());
        sb.append("\n");

        sb.append(prefix(level + 1));
        sb.append(getCompletionExpressionLocation().describe());
        sb.append("  completion-expression: ");
        sb.append(getCompletionExpression());

        return sb.toString();
    }
}
