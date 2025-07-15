package com.siglet.container.engine.pipeline.processor.siglet.spanlet.unmodifiable;


import com.siglet.SigletError;
import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpanlet;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.engine.Context;
import com.siglet.container.eventloop.processor.BaseEventloopProcessor;
import com.siglet.container.eventloop.processor.result.ResultFactoryImpl;

public class UnmodifiedSpanBaseEventloopProcessor<T> extends BaseEventloopProcessor<T> {

    private final UnmodifiableSpanlet<T> spanlet;

    private final Context context;

    public UnmodifiedSpanBaseEventloopProcessor(Context context, ProcessorContext<T> processorContext,
                                                UnmodifiableSpanlet<T> spanlet) {
        super(processorContext, ResultFactoryImpl.INSTANCE);
        this.context = context;
        this.spanlet = spanlet;
    }

    @Override
    protected Result process(Signal signal, ProcessorContext<T> processorContext, ResultFactory resultFactory) {

        if (!(signal instanceof ProtoSpanAdapter protoSpanAdapter)) {
            throw new SigletError(String.format("Signal (%s) is %s and Unmodifiable spanlets can only process Span " +
                    "signals", signal.getId(), signal.getClass().getName()));

        }
        ProtoSpanAdapter protoSpanToProcess;
        if (protoSpanAdapter.isUpdated()) {
            protoSpanToProcess = context.getSpanObjectPool().get(protoSpanAdapter.getUpdated(),
                    protoSpanAdapter.getUpdatedInstrumentationScope(), protoSpanAdapter.getUpdatedResource());
            context.getSpanObjectPool().recycle(protoSpanAdapter);
        } else {
            protoSpanToProcess = protoSpanAdapter;
        }
        try {
            return spanlet.span(protoSpanToProcess, processorContext, resultFactory);
        } finally {
            if (protoSpanToProcess.isUpdated()) {
                protoSpanToProcess.clearChanges();
            }
        }

    }
}
