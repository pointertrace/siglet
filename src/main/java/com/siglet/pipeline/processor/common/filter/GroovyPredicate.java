package com.siglet.pipeline.processor.common.filter;

import com.siglet.SigletError;
import com.siglet.pipeline.processor.common.action.groovy.ShellCreator;
import groovy.lang.Script;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class GroovyPredicate implements Predicate {

    private final Script script;

    //TODO para singleton
    private final ShellCreator shellCreator = new ShellCreator();

    public GroovyPredicate(String script) {
        this.script = shellCreator.compile(script);
    }

    @Override
    public boolean matches(Exchange exchange) {
        shellCreator.prepareScript(script, exchange.getIn().getBody());
        Object result = script.run();
        if (!(result instanceof Boolean bool)) {
            throw new SigletError("groovy expression for a filter must return a boolean not a " +
                    result.getClass().getName());

        } else {
            return bool;
        }
    }
}
