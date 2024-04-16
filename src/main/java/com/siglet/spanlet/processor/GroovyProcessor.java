package com.siglet.spanlet.processor;

import com.siglet.spanlet.GroovyPropertySetter;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class GroovyProcessor implements Processor {

    private final Script script;

    private final GroovyPropertySetter groovyPropertySetter;

    public GroovyProcessor(String script, GroovyPropertySetter groovyPropertySetter) {
        this.script = new GroovyShell().parse(script);
        this.groovyPropertySetter = groovyPropertySetter;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        groovyPropertySetter.setBodyInScript(exchange, script);
        script.run();

    }
}
