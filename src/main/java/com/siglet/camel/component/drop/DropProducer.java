package com.siglet.camel.component.drop;

import com.siglet.SigletError;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTrace;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

public class DropProducer extends DefaultProducer {

    public DropProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("\n\n\n");
        System.out.println("===== Dropped exchange");
        Object body = exchange.getIn().getBody();
        System.out.println("type:" + body.getClass().getName());
        switch (body) {
            case ProtoSpanAdapter spanAdapter -> {
                System.out.println("span.traceId=" + spanAdapter.getTraceIdEx());
                System.out.println("span.spanId=" + spanAdapter.getSpanIdEx());
            }
            case ProtoTrace traceAdapter -> {
                System.out.println("trace.traceId=" + traceAdapter.getTraceIdEx());
                System.out.println("num spans=" + traceAdapter.getSize());
                traceAdapter.forEachSpan(span -> {
                    System.out.println("   spanId=" + span.getSpanIdEx());
                });
            }
            case ProtoMetricAdapter metricAdapter -> System.out.println("metric.name=" + metricAdapter.getName());
            default -> throw new SigletError("exchange contains invalid body type:" + body.getClass().getName());
        }
        System.out.println("\n\n\n");
    }
}
