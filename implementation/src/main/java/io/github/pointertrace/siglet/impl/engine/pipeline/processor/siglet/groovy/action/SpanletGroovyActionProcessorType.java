package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.action;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.config.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.BaseSigletProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.BindingUtils;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.Compiler;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.GroovyProcessor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;

public class SpanletGroovyActionProcessorType implements ProcessorType<GroovyActionConfig> {

    @Override
    public String getType() {
        return "spanlet-groovy-action";
    }

    @Override
    public ConfigurationFactory<GroovyActionConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(property("action", GroovyActionConfig::setAction, string())), GroovyActionConfig.class
        );
    }

    @Override
    public ComponentCreator<ProcessorNode> getComponentCreator() {
        return (sigletContext, node) -> new GroovyProcessor(sigletContext, node,
                createSpanletFunctionFactory(node));
    }

    public static Supplier<BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult>> createSpanletFunctionFactory(ProcessorNode node) {
        if (node.getDescription().getConfig() instanceof GroovyActionConfig groovyActionConfig) {
            return createSpanletFunctionFactory(groovyActionConfig);
        } else {
            throw new SigletError(String.format("for groovy action type config must be a GroovyActionConfig but it is %s",
                    node.getDescription().getConfig().getClass().getName()));
        }
    }

    public static Supplier<BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult>> createSpanletFunctionFactory(GroovyActionConfig groovyActionConfig) {
        Compiler compiler = new Compiler();
        // Compile once. Supplier called once per pool thread — each thread gets its own Script.
        Script template = compiler.compile(groovyActionConfig.getAction());
        return () -> {
            Script compiledActionScript = compiler.newInstance(template);
            return (Signal signal, Context<?> context) -> {
                compiler.prepareScript(compiledActionScript, signal, context);
                compiledActionScript.run();
                Result result = BindingUtils.getResult(compiledActionScript.getBinding());
                Map<String, List<Signal>> routes = BindingUtils.getRoutes(compiledActionScript.getBinding());
                for (Map.Entry<String, List<Signal>> entry : routes.entrySet()) {
                    for (Signal s : entry.getValue()) {
                        result = result.andSend(s, entry.getKey());
                    }
                }
                return new BaseSigletProcessor.ProcessResult(signal, result);
            };
        };
    }

    // kept for backward compatibility
    public static BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult> createSpanletFunction(ProcessorNode node) {
        return createSpanletFunctionFactory(node).get();
    }

    public static BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult> createSpanletFunction(GroovyActionConfig groovyActionConfig) {
        return createSpanletFunctionFactory(groovyActionConfig).get();
    }
}
