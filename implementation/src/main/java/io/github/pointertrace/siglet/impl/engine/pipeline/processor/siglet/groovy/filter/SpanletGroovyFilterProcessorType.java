package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.filter;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.component.config.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.BaseSigletProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.Compiler;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.GroovyProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultFactoryImpl;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;

public class SpanletGroovyFilterProcessorType implements ProcessorType<GroovyFilterConfig> {

    @Override
    public String getType() {
        return "spanlet-groovy-filter";
    }

    @Override
    public ConfigurationFactory<GroovyFilterConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(property("expression", GroovyFilterConfig::setExpression, string())), GroovyFilterConfig.class
        );
    }

    @Override
    public ComponentCreator<ProcessorNode> getComponentCreator() {
        return (sigletContext, node) -> new GroovyProcessor(sigletContext, node,
                createSpanletFunctionFactory(node));
    }

    /**
     * Returns a Supplier that compiles the script once and creates a new
     * BiFunction (with its own Script instance) on each call.
     * GroovyProcessor calls this supplier once per pool thread.
     */
    public static Supplier<BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult>> createSpanletFunctionFactory(ProcessorNode node) {
        if (node.getDescription().getConfig() instanceof GroovyFilterConfig groovyFilterConfig) {
            return createSpanletFunctionFactory(groovyFilterConfig);
        } else {
            throw new IllegalArgumentException(String.format("for groovy filter type config must be a GroovyFilterConfig but it is %s",
                    node.getDescription().getConfig().getClass().getName()));
        }
    }

    public static Supplier<BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult>> createSpanletFunctionFactory(GroovyFilterConfig groovyFilterConfig) {
        Compiler compiler = new Compiler();
        // Compile once — expensive. The supplier is called once per pool thread.
        Script template = compiler.compile(groovyFilterConfig.getExpression());
        ResultFactory resultFactory = ResultFactoryImpl.getInstance();
        return () -> {
            // Each call produces a new BiFunction that owns its own Script instance.
            Script predicateScript = compiler.newInstance(template);
            return (Signal signal, Context<?> context) -> {
                compiler.prepareScript(predicateScript, signal, context);
                Object predicate = predicateScript.run();
                if (predicate instanceof Boolean boolPredicate && boolPredicate) {
                    return new BaseSigletProcessor.ProcessResult(signal, resultFactory.proceed());
                } else {
                    return new BaseSigletProcessor.ProcessResult(signal, resultFactory.drop());
                }
            };
        };
    }

    public static BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult> createSpanletFunction(GroovyFilterConfig groovyFilterConfig) {
        return createSpanletFunctionFactory(groovyFilterConfig).get();
    }
}
