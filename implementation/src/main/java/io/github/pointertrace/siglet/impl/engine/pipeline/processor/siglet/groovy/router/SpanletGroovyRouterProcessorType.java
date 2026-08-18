package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.router;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.*;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.component.config.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.BaseSigletProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.Compiler;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.GroovyProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class SpanletGroovyRouterProcessorType implements ProcessorType<GroovyRouterConfig> {

    @Override
    public String getType() {
        return "spanlet-groovy-router";
    }

    @Override
    public ConfigurationFactory<GroovyRouterConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(property("default", GroovyRouterConfig::setDefaultRoute, stringValueObject()),
                        property("routes", GroovyRouterConfig::setRoutes,
                                array(ArrayList::new, arrayItem(List::add, object(RouteConfig::new)
                                        .addProperty(property("to", RouteConfig::setTo, stringValueObject()))
                                        .addProperty(property("when", RouteConfig::setWhen, stringValueObject()))))
                        )
                ), GroovyRouterConfig.class);
    }

    @Override
    public ComponentCreator<ProcessorNode> getComponentCreator() {
        return (sigletContext, node) -> new GroovyProcessor(sigletContext, node,
                createSpanletFunctionFactory((GroovyRouterConfig) node.getDescription().getConfig()));
    }

    public static Supplier<BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult>> createSpanletFunctionFactory(ProcessorNode node) {
        if (node.getDescription().getConfig() instanceof GroovyRouterConfig groovyRouterConfig) {
            return createSpanletFunctionFactory(groovyRouterConfig);
        } else {
            throw new SigletError(String.format("for groovy router type config must be a GroovyRouterConfig but it is %s",
                    node.getDescription().getConfig().getClass().getName()));
        }
    }

    public static Supplier<BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult>> createSpanletFunctionFactory(GroovyRouterConfig groovyRouterConfig) {
        Compiler compiler = new Compiler();
        // Compile each predicate once. Supplier called once per pool thread —
        // each thread gets its own Script instances for all routes.
        List<RouteTemplate> templates = groovyRouterConfig.getRoutes().stream()
                .map(r -> new RouteTemplate(compiler.compile(r.getWhen().getValue()), r.getTo().getValue()))
                .toList();
        ResultFactory resultFactory = ResultFactoryImpl.getInstance();
        String defaultDestination = groovyRouterConfig.getDefaultRoute().getValue();
        return () -> {
            List<CompiledRoute> threadRoutes = templates.stream()
                    .map(t -> new CompiledRoute(compiler.newInstance(t.template()), t.destination()))
                    .toList();
            return (Signal signal, Context<?> context) -> {
                for (CompiledRoute route : threadRoutes) {
                    compiler.prepareScript(route.predicate(), signal, context);
                    Boolean match = (Boolean) route.predicate().run();
                    if (match) {
                        return new BaseSigletProcessor.ProcessResult(signal, resultFactory.proceed(route.destination()));
                    }
                }
                return new BaseSigletProcessor.ProcessResult(signal, resultFactory.proceed(defaultDestination));
            };
        };
    }

    /** Holds the compiled Script class (template) for one route. */
    private record RouteTemplate(Script template, String destination) {}

    /** Holds a per-thread Script instance for one route. */
    private record CompiledRoute(Script predicate, String destination) {}
}
