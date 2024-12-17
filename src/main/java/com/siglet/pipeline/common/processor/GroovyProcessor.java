package com.siglet.pipeline.common.processor;

import com.siglet.pipeline.common.processor.groovy.ShellCreator;
import groovy.lang.Script;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class GroovyProcessor implements Processor {

    private final Script script;

    private final ShellCreator shellCreator = new ShellCreator();

    public GroovyProcessor(String script) {
        this.script = shellCreator.compile(script);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        shellCreator.prepareScript(script, exchange.getIn().getBody());
        script.run();
    }
}
