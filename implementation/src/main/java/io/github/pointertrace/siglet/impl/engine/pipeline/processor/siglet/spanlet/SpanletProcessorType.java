package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.engine.component.config.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.BaseSigletProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultFactoryImpl;

import java.util.function.BiFunction;

public class SpanletProcessorType<T> implements ProcessorType<T> {

    private final SigletDefinition sigletDefinition;

    public SpanletProcessorType(SigletDefinition sigletDefinition) {
        this.sigletDefinition = sigletDefinition;
    }

    @Override
    public String getType() {
        return sigletDefinition.getName();
    }

    @Override
    public ConfigurationFactory<T> getConfigurationFactory() {
        return (ConfigurationFactory<T>) sigletDefinition.createConfigurationFactory();
    }

    @Override
    public ComponentCreator<ProcessorNode> getComponentCreator() {
        return (sigletContext, node) ->
                new SpanletProcessor(sigletContext, node, createSpanletFunction(sigletDefinition.createProcessor()));
    }


    public static BiFunction<Span, Context<?>, BaseSigletProcessor.ProcessResult> createSpanletFunction(Spanlet<?> spanlet) {
        ResultFactory resultFactory = ResultFactoryImpl.getInstance();
        return (span, context) -> new BaseSigletProcessor.ProcessResult(span, processSpan(spanlet, span, context, resultFactory));
    }

    @SuppressWarnings("unchecked")
    private static <C> Result processSpan(Spanlet<C> spanlet, Span span, Context<?> context, ResultFactory resultFactory) {
        return spanlet.span(span, (Context<C>) context, resultFactory);
    }


}
