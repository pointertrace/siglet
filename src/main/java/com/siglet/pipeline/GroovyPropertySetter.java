package com.siglet.pipeline;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTraceAdapter;
import groovy.lang.Script;
import org.apache.camel.Exchange;

public interface GroovyPropertySetter {

    GroovyPropertySetter span = new SpanPropertySetter();

    GroovyPropertySetter trace = new TracePropertySetter();

    GroovyPropertySetter metric = new MetricPropertySetter();

    void setBodyInScript(Exchange exchange, Script script);

    default void setSenderInScript(Exchange exchange, Script script) {
        script.setProperty("sender", GroovySignalSender.create(exchange));
    }

    default void setSignalCreatorInScript(Exchange exchange, Script script) {
        script.setProperty("signalCreator",GroovySignalCreator.create(exchange));
    }

    class SpanPropertySetter implements GroovyPropertySetter {

        @Override
        public void setBodyInScript(Exchange exchange, Script script) {
            script.setProperty("span", exchange.getIn().getBody(ProtoSpanAdapter.class));
            setSenderInScript(exchange, script);
            setSignalCreatorInScript(exchange, script);
        }
    }

    class TracePropertySetter implements GroovyPropertySetter {

        @Override
        public void setBodyInScript(Exchange exchange, Script script) {
            script.setProperty("trace", exchange.getIn().getBody(ProtoTraceAdapter.class));
            setSenderInScript(exchange, script);
            setSignalCreatorInScript(exchange, script);
        }
    }

    class MetricPropertySetter implements GroovyPropertySetter {

        @Override
        public void setBodyInScript(Exchange exchange, Script script) {
            script.setProperty("metric", exchange.getIn().getBody(ProtoMetricAdapter.class));
            setSenderInScript(exchange, script);
            setSignalCreatorInScript(exchange, script);
        }
    }

}
