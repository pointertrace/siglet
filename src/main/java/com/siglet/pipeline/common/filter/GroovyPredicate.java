package com.siglet.pipeline.common.filter;

import com.siglet.SigletError;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class GroovyPredicate implements Predicate {

    private final Script script;

    public GroovyPredicate(String script) {
        this.script = new GroovyShell().parse(script);
    }

    @Override
    public boolean matches(Exchange exchange) {
        script.setProperty("thisSignal", exchange.getIn().getBody());
        Object result = script.run();
        if (!(result instanceof Boolean bool)) {
            throw new SigletError("groovy expression for a filter must return a boolean not a " +
                    result.getClass().getName());

        } else {
            return bool;
        }
    }
}
