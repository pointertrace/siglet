package com.siglet.pipeline.processor.common.filter;

import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;

public class FilterConfig extends Item {

    private String expression;

    private Location expressionLocation;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Location getExpressionLocation() {
        return expressionLocation;
    }

    public void setExpressionLocation(Location expressionLocation) {
        this.expressionLocation = expressionLocation;
    }
}
