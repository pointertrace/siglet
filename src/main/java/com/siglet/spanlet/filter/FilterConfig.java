package com.siglet.spanlet.filter;

import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;

public class FilterConfig extends Item {

    private ValueItem<String> expression;

    public ValueItem<String> getExpression() {
        return expression;
    }

    public void setExpression(ValueItem<String> expression) {
        this.expression = expression;
    }
}
