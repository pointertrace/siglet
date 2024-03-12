package org.apache.camel.example;

import com.siglet.data.modifiable.ModifiableSpan;
import com.siglet.data.modifiable.ModifiableSpanletProcessor;

public class AddParameterSpanletProcessor implements ModifiableSpanletProcessor {
    @Override
    public void span(ModifiableSpan span, Object config) {
        if ("lets-go".equals(span.getName())) {
            span.getAttributes().set("primeiro", true);
        } else{
            span.getAttributes().set("segundo", true);
        }
        span.getAttributes().set("span-attribute", "span-attribute-value");
        span.getResource().getAttributes().set("resource-attribute", "resource-attribute-value");
        span.getInstrumentationScope().getAttributes().set("instrumentation-attribute", "instrumentation-attribute-value");
    }
}
