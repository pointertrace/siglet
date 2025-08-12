package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.router;

public class SpanletGroovyRouterProcessorType extends BaseGroovyRouterProcessorType {

    private final GroovyRouterDefinition routerDefinition = new GroovyRouterDefinition();

    @Override
    public String getName() {
        return "spanlet-groovy-router";
    }

}
