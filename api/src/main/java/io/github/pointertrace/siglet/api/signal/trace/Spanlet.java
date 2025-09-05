package io.github.pointertrace.siglet.api.signal.trace;

import io.github.pointertrace.siglet.api.Processor;
import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;

public interface Spanlet<T> extends Processor {

    Result span(Span span, ProcessorContext<T> config, ResultFactory resultFactory);

}
