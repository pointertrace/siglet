package com.siglet.spanlet.filter;

import com.siglet.SigletError;
import com.siglet.spanlet.GroovyPropertySetter;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

public class GroovyPredicate implements Predicate {

    private final Script script;
    private final GroovyPropertySetter groovyPropertySetter;

    public GroovyPredicate(String script, GroovyPropertySetter groovyPropertySetter) {
        this.script = new GroovyShell().parse(script);
        this.groovyPropertySetter = groovyPropertySetter;
    }

    @Override
    public boolean matches(Exchange exchange) {
        groovyPropertySetter.setBodyInScript(exchange, script);
        Object result = script.run();
        if (!(result instanceof Boolean bool)) {
            throw new SigletError("groovy expression for a filter must return a boolean not a " +
                    result.getClass().getName());

        } else {
            return bool;
        }
    }
}
