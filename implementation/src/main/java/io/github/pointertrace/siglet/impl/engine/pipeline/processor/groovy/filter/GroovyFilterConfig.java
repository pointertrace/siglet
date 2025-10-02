package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

public class GroovyFilterConfig implements Located, Describable {

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
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getLocation().describe());
        sb.append("  groovyFilterConfig:");

        sb.append("\n");
        sb.append(Describable.prefix(level + 1));
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
