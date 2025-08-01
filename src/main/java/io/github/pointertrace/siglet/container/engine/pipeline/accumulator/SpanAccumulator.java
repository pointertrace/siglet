package io.github.pointertrace.siglet.container.engine.pipeline.accumulator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.engine.Context;

import java.util.List;

public class SpanAccumulator {

    private SpanAccumulator() {
    }

    public static AccumulatedSpans accumulateSpans(Context context, List<Signal> signals) {
        SpansAccumulator spansAccumulator = new SpansAccumulator();
        StringBuilder sb = new StringBuilder("Aggregated Spans[");
        signals.forEach(signal -> {
            if (signal instanceof ProtoSpanAdapter protoSpanAdapter) {
                sb.append(protoSpanAdapter.getSpanId());
                spansAccumulator.add(protoSpanAdapter.getUpdated(), protoSpanAdapter.getUpdatedInstrumentationScope(),
                        protoSpanAdapter.getUpdatedResource());
                context.getSpanObjectPool().recycle(protoSpanAdapter);
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal %s is %s", signal.getId(),
                        signal.getClass().getName()));
            }
        });
        sb.append("]");
        return new AccumulatedSpans(spansAccumulator.getExportTraceServiceRequest(), sb.toString());
    }

}
