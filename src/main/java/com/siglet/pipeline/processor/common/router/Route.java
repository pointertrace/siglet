package com.siglet.pipeline.processor.common.router;

import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;

public class Route extends Item {

    private String expression;

    private Location expressionLocation;

    private String to;

    private Location toLocation;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Location getExpressionLocation() {
        return expressionLocation;
    }

    public void setExpressionLocation(Location expressionLocation) {
        this.expressionLocation = expressionLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }
}
