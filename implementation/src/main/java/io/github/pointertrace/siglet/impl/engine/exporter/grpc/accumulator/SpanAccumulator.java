package io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;

public class SpanAccumulator {

    private SpanAccumulator() {
    }

    public static AccumulatedSpans accumulateSpans(Object[] spans) {
        SpansAccumulator spansAccumulator = new SpansAccumulator();
        for (Object span : spans) {
            if (span instanceof SpanAdapter spanAdapter) {
                spansAccumulator.add(spanAdapter.getUpdated(), spanAdapter.getUpdatedScope(),
                        spanAdapter.getUpdatedResource());
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal is %s", span.getClass().getName()));
            }
        }
        return new AccumulatedSpans(spansAccumulator.getExportTraceServiceRequest(), spans.length);
    }

}
