package com.siglet.container.engine.pipeline.accumulator;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;

import java.util.List;

public class SpanAccumulator {

    private SpanAccumulator() {
    }

    public static AccumulatedSpans accumulateSpans(List<Signal> signals) {
        SignalsAccumulator signalsAccumulator = new SignalsAccumulator();
        StringBuilder sb = new StringBuilder("Aggregated Spans[");
        signals.forEach(signal -> {
            if (signal instanceof ProtoSpanAdapter protoSpanAdapter) {
                sb.append(protoSpanAdapter.getSpanId());
                signalsAccumulator.add(protoSpanAdapter.getUpdated(), protoSpanAdapter.getUpdatedInstrumentationScope(),
                        protoSpanAdapter.getUpdatedResource());
            } else {
                throw new SigletError(String.format("Can only aggregate spans but signal %s is %s", signal.getId(),
                        signal.getClass().getName()));
            }
        });
        sb.append("]");
        return new AccumulatedSpans(signalsAccumulator.getExportTraceServiceRequest(), sb.toString());
    }

}
