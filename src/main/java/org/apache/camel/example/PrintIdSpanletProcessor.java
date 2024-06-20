package org.apache.camel.example;

import com.siglet.data.unmodifiable.trace.UnmodifiableSpan;
import com.siglet.data.unmodifiable.trace.UnmodifiableSpanletProcessor;

public class PrintIdSpanletProcessor implements UnmodifiableSpanletProcessor {

    private final String prefix;

    public PrintIdSpanletProcessor(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void span(UnmodifiableSpan span, Object config) {
        System.out.printf("%s - traceIdHigh:%d, traceIdLow:%d, spanId:%d%n",prefix, span.getTraceIdHigh(),
        span.getTraceIdLow(), span.getSpanId());

    }
}
