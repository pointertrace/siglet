package io.github.pointertrace.siglet.impl.engine.pipeline.accumulator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.engine.SigletContext;

import java.util.List;

public class SpanAccumulator {

    private SpanAccumulator() {
    }

    public static AccumulatedSpans accumulateSpans(SigletContext sigletContext, Signal[] signals) {
        SpansAccumulator spansAccumulator = new SpansAccumulator();
        StringBuilder sb = new StringBuilder("Aggregated Spans[");
        for (Signal signal : signals) {
            if (signal instanceof SpanAdapter spanAdapter) {
                sb.append(spanAdapter.getSpanId());
                spansAccumulator.add(spanAdapter.getUpdated(), spanAdapter.getUpdatedScope(),
                        spanAdapter.getUpdatedResource());
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal %s is %s", signal.getId(),
                        signal.getClass().getName()));
            }
        }
        sb.append("]");
        return new AccumulatedSpans(spansAccumulator.getExportTraceServiceRequest(), sb.toString());
    }

}
