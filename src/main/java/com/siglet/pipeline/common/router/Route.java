package com.siglet.pipeline.common.router;

import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;

public class Route extends Item {

    private ValueItem<String> expression;

    private ValueItem<String> to;

    public ValueItem<String> getExpression() {
        return expression;
    }

    public void setExpression(ValueItem<String> expression) {
        this.expression = expression;
    }

    public ValueItem<String> getTo() {
        return to;
    }

    public void setTo(ValueItem<String> to) {
        this.to = to;
    }

}
