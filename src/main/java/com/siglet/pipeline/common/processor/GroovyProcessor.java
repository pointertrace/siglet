package com.siglet.pipeline.common.processor;

import com.siglet.pipeline.common.processor.groovy.ShellCreator;
import groovy.lang.Script;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class GroovyProcessor implements Processor {

    private final Script script;

    private final ShellCreator shellCreator = new ShellCreator();

    private final CamelContext camelContext;

    public GroovyProcessor(CamelContext camelContext, String script) {
        this.camelContext = camelContext;
        this.script = shellCreator.compile(script);
        this.script.setProperty("context", camelContext);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        script.getBinding().setProperty("thisSignal", exchange.getIn().getBody());
        script.run();
    }
}
