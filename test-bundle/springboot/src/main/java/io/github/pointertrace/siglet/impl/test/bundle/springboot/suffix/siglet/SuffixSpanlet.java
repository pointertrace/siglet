package io.github.pointertrace.siglet.impl.test.bundle.springboot.suffix.siglet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import org.springframework.stereotype.Component;

@Component
public class SuffixSpanlet implements Spanlet<SuffixSpanletConfig> {

    @Override
    public Result span(Span span, Context<SuffixSpanletConfig> context, ResultFactory resultFactory) {
        span.setName(span.getName() + context.getConfig().getSuffix() + "-springboot-uberjar");
        return resultFactory.proceed();
    }

}
