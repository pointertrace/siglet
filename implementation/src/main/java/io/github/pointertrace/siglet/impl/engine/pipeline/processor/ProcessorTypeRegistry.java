package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.impl.engine.ComponentTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action.SpanletGroovyActionProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter.SpanletGroovyFilterProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router.SpanletGroovyRouterProcessorType;

public class ProcessorTypeRegistry extends ComponentTypeRegistry<ProcessorType<?>> {


    public ProcessorTypeRegistry() {
        register(new SpanletGroovyActionProcessorType());
        register(new SpanletGroovyFilterProcessorType());
        register(new SpanletGroovyRouterProcessorType());
    }

}
