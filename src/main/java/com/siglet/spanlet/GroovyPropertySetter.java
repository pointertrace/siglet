package com.siglet.spanlet;

import com.siglet.data.adapter.ProtoSpanAdapter;
import com.siglet.data.adapter.ProtoTraceAdapter;
import groovy.lang.Script;
import org.apache.camel.Exchange;

public interface GroovyPropertySetter {

    GroovyPropertySetter span = new SpanPropertySetter();

    GroovyPropertySetter trace = new TracePropertySetter();

    void setBodyInScript(Exchange exchange, Script script);

    class SpanPropertySetter implements GroovyPropertySetter {

        @Override
        public void setBodyInScript(Exchange exchange, Script script) {
            script.setProperty("span", exchange.getIn().getBody(ProtoSpanAdapter.class));
        }
    }

    class TracePropertySetter implements GroovyPropertySetter {

        @Override
        public void setBodyInScript(Exchange exchange, Script script) {
            script.setProperty("trace", exchange.getIn().getBody(ProtoTraceAdapter.class));
        }

    }
}
