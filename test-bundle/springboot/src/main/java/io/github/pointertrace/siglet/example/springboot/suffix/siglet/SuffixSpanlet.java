package io.github.pointertrace.siglet.example.springboot.suffix.siglet;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import org.springframework.stereotype.Component;

@Component
public class SuffixSpanlet implements Spanlet<SuffixSpanletConfig> {

    @Override
    public Result span(Span span, ProcessorContext<SuffixSpanletConfig> processorContext, ResultFactory resultFactory) {
        span.setName(span.getName() + processorContext.getConfig().getSuffix() + "-springboot-uberjar");
        return resultFactory.proceed();
    }

}
