package com.siglet.pipeline.processor.common.filter;

import com.siglet.config.item.Describable;
import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

public class FilterConfig  implements Located, Describable {

    private Location location;

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

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe());
        sb.append("  filterConfig:");

        sb.append("\n");
        sb.append(prefix(level + 1));
        sb.append(getExpressionLocation().describe());
        sb.append("  expression: ");
        sb.append(getExpression());

        return sb.toString();
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;

    }
}
