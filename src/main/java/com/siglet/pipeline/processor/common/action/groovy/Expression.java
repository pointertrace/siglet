package com.siglet.pipeline.processor.common.action.groovy;

import groovy.lang.Closure;

public class Expression {

    public static final Expression FALSE_EXPRESSION = new Expression(false);
    public static final Expression TRUE_EXPRESSION = new Expression(true);

    private final boolean value;

    private Expression(boolean value) {
        this.value = value;
    }

    public void then(Closure<Void> closure){
        if (value) {
            closure.call();
        }
    }
}
