package com.siglet.container.engine.pipeline.processor;

import com.siglet.SigletError;
import com.siglet.api.modifiable.trace.ModifiableSpanlet;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpanlet;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.config.raw.ProcessorKind;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.pipeline.processor.groovy.action.GroovyActionConfig;
import com.siglet.container.engine.pipeline.processor.groovy.action.GroovyActionDefinition;
import com.siglet.container.engine.pipeline.processor.groovy.action.GroovyActionProcessor;
import com.siglet.container.engine.pipeline.processor.groovy.filter.GroovyFilterConfig;
import com.siglet.container.engine.pipeline.processor.groovy.filter.GroovyFilterDefinition;
import com.siglet.container.engine.pipeline.processor.groovy.filter.GroovyFilterProcessor;
import com.siglet.container.engine.pipeline.processor.groovy.router.GroovyRouterConfig;
import com.siglet.container.engine.pipeline.processor.groovy.router.GroovyRouterDefinition;
import com.siglet.container.engine.pipeline.processor.groovy.router.GroovyRouterProcessor;
import com.siglet.container.engine.pipeline.processor.groovy.router.Route;
import com.siglet.container.engine.pipeline.processor.siglet.spanlet.modifiable.ModifiableSpanletProcessor;
import com.siglet.container.engine.pipeline.processor.siglet.spanlet.unmodifiable.UnmodifiableSpanletProcessor;

import java.util.*;

import static com.siglet.parser.schema.SchemaFactory.requiredProperty;

public class ProcessorTypes {

    private static final ProcessorTypes INSTANCE = new ProcessorTypes();

    public static ProcessorTypes getInstance() {
        return INSTANCE;
    }

    private final Map<String, ProcessorType> definitions = new HashMap<>();

    private ProcessorTypes() {
        add(new ProcessorType("groovy-action", new GroovyActionDefinition(), (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyActionConfig) {
                return new GroovyActionProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getConfig().getClass().getName()));
            }
        }));
        add(new ProcessorType("groovy-filter", new GroovyFilterDefinition(), (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyFilterConfig) {
                return new GroovyFilterProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getConfig().getConfig().getClass().getName()));
            }
        }));
        add(new ProcessorType("groovy-router", new GroovyRouterDefinition(), (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyRouterConfig groovyRouterConfig) {
                return new GroovyRouterProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getConfig().getConfig().getClass().getName()));
            }
        }));
    }

    public void add(ProcessorType processorType) {
        definitions.put(processorType.getName(), processorType);
    }

    public ProcessorType get(String type) {
        return definitions.get(type);
    }

    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(definitions.keySet());

    }

    public void add(com.siglet.container.config.siglet.SigletConfig sigletConfig) {
        definitions.put(
                sigletConfig.name(),
                new ProcessorType(sigletConfig.name(), createConfigDefinition(sigletConfig), new ProcessorCreator() {
                    @Override
                    public Processor create(Context context, ProcessorNode node) {
                        if (node.getConfig().getKind() == ProcessorKind.SPANLET) {
                            com.siglet.api.Processor instance = sigletConfig.createSigletInstance();
                            if (instance instanceof ModifiableSpanlet modifiableSpanlet) {
                                return new ModifiableSpanletProcessor(context, node, modifiableSpanlet);
                            } else if (instance instanceof UnmodifiableSpanlet unmodifiableSpanlet) {
                                return new UnmodifiableSpanletProcessor(context, node, unmodifiableSpanlet);
                            }
                        }
                        throw new SigletError(String.format("Cannot create siglet type %s", node.getName()));
                    }
                }));
    }

    public void add(List<com.siglet.container.config.siglet.SigletConfig> processorTypes) {
        processorTypes.forEach(this::add);
    }

    private ConfigDefinition createConfigDefinition(com.siglet.container.config.siglet.SigletConfig sigletConfig) {
        return () -> requiredProperty(ProcessorConfig::setConfig, ProcessorConfig::setConfigLocation, "config",
                sigletConfig.configChecker());
    }


    public Processor create(Context context, ProcessorNode node) {
        ProcessorType processorType = definitions.get(node.getConfig().getType());
        if (processorType == null) {
            throw new SigletError("Processor type " + node.getConfig().getType() + " not found");
        }
        return processorType.getProcessorCreator().create(context, node);
    }


}
