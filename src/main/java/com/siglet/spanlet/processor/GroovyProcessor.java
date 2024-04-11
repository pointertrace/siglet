package com.siglet.spanlet.processor;

import com.siglet.data.adapter.ProtoSpanAdapter;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class GroovyProcessor implements Processor {

    private final Script script;

    private String name;


    public GroovyProcessor(String script) {
        this(null, script);

    }

    public GroovyProcessor(String name, String script) {
        this.name = name;
        this.script = new GroovyShell().parse(script);
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        script.setProperty("span",exchange.getIn().getBody(ProtoSpanAdapter.class));
        script.run();

    }
}
