package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationImpl;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSourceImpl;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.BaseProcessor;
import io.github.pointertrace.siglet.impl.eventloop.ReceiveFunction;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorEventLoop;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class BaseSigletProcessor<T extends Signal> extends BaseProcessor {

    private final ProcessorEventLoop<T, ProcessResult> processorEventLoop;

    private final Context<?> spanletContext;

    private SignalEmitterFunction signalEmitter;

    private SignalDestination signalDestination;

    private SignalSource signalSource;

    private final ReceiveFunction<T> eventLoopReceiver;

    public BaseSigletProcessor(SigletContext sigletContext, ProcessorNode node,
                               BiFunction<T, Context<?>, ProcessResult> sigletExecutionFunction) {
        this(sigletContext, node, () -> sigletExecutionFunction);
    }

    /**
     * Constructor for processors that need a distinct function instance per pool thread
     * (e.g. Groovy processors with thread-unsafe Script state).
     * The factory is called once per thread, before the processing loop.
     */
    public BaseSigletProcessor(SigletContext sigletContext, ProcessorNode node,
                               Supplier<BiFunction<T, Context<?>, ProcessResult>> sigletExecutionFunctionFactory) {
        super(sigletContext, node);
        this.spanletContext = new ContextImpl<>(node.getDescription().getConfig());
        processorEventLoop = new ProcessorEventLoop<>(this,
                sigletContext.getConfig().getQueueSize(node.getDescription()),
                sigletContext.getConfig().getThreadPoolSize(node.getDescription()),
                sigletContext.getInterceptor(),
                () -> {
                    BiFunction<T, Context<?>, ProcessResult> fn = sigletExecutionFunctionFactory.get();
                    return (span) -> fn.apply(span, spanletContext);
                },
                this::emmit);
        eventLoopReceiver = processorEventLoop.getReceiver();
    }

    @Override
    public void doStart() {
        processorEventLoop.start();
    }

    @Override
    public void doStop() {
        processorEventLoop.stop();
    }

    public Context<?> getContext() {
        return spanletContext;
    }

    public boolean receive(Object signal) {
        return eventLoopReceiver.receive(castSignal(signal));
    }

    protected abstract T castSignal(Object signal);

    public void setSignalEmitterFunction(SignalEmitterFunction signalEmitterFunction) {
        if (signalDestination != null) {
            throw new SigletError("SignalSource already created");
        }
        this.signalEmitter = signalEmitterFunction;
    }

    void emmit(ProcessResult processResult) {
        if (processResult.result() instanceof ResultImpl resultImpl) {
            resultImpl.emit(getNode().getDestinationMappings(), signalEmitter, processResult.signal());
        } else {
            throw new SigletError(String.format("Result must be type %s but it is %s",
                    ResultImpl.class, processResult.result().getClass()));
        }
    }

    @Override
    public SignalDestination getSignalDestination() {
        if (signalDestination == null) {
            signalDestination = new SignalDestinationImpl(this, this::receive);
        }
        return signalDestination;
    }

    @Override
    public SignalSource getSignalSource() {
        if (signalSource == null) {
            if (signalEmitter != null) {
                throw new SigletError("SignalEmitterFunction already set");
            }
            signalSource = new SignalSourceImpl(this);
            signalEmitter = signalSource.getSignalEmitterFunction();
        }
        return signalSource;
    }

    public record ProcessResult(Object signal, Result result) {
    }

}
