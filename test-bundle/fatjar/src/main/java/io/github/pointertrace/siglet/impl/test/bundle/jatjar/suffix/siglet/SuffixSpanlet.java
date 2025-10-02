package io.github.pointertrace.siglet.impl.test.bundle.jatjar.suffix.siglet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;

public class SuffixSpanlet implements Spanlet<SuffixSpanletConfig> {


    @Override
    public Result span(Span span, Context<SuffixSpanletConfig> context, ResultFactory resultFactory) {
        span.setName(span.getName() + context.getConfig().getSuffix() + "-fatjar");
        return resultFactory.proceed();
    }

}
