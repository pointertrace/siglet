package io.github.pointertrace.siglet.api.signal.trace;

import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;

public interface Spanlet<T> extends Siglet {

    Result span(Span span, Context<T> config, ResultFactory resultFactory);

}
