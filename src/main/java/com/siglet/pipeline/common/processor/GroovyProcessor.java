package com.siglet.pipeline.common.processor;

import com.siglet.pipeline.GroovyPropertySetter;
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
        // criar um sender buscando a rota pelo nome e envidando via:
        //   producerTemplate.sendBody("direct:nome-da-rota", mensagem);
    }
}
