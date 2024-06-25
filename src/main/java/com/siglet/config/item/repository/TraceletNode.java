package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.TraceletItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;
import com.siglet.pipeline.GroovyPropertySetter;
import com.siglet.pipeline.common.filter.FilterConfig;
import com.siglet.pipeline.common.filter.GroovyPredicate;
import com.siglet.pipeline.common.processor.GroovyProcessor;
import com.siglet.pipeline.common.processor.ProcessorConfig;
import com.siglet.pipeline.common.router.Route;
import com.siglet.pipeline.common.router.RouterConfig;


public class TraceletNode extends ProcessorNode<TraceletItem> {

    public TraceletNode(String name, TraceletItem item) {
        super(name, item);
    }

    public void createRoute(RouteCreator routeCreator) {
        Object config = getItem().getConfig();
        switch (getItem().getType().getValue()) {
            case "processor":
                if (!(config instanceof ProcessorConfig processorConfig)) {
                    throw new SigletError("");
                }
                if (getTo().size() == 1) {
                    getTo().getFirst().createRoute(routeCreator.
                            addProcessor(new GroovyProcessor(processorConfig.getAction().getValue(),
                                    GroovyPropertySetter.trace)));
                } else {
                    RouteCreator multicast = routeCreator.addProcessor(
                            new GroovyProcessor(processorConfig.getAction().getValue(),
                                    GroovyPropertySetter.trace)).startMulticast();
                    for (Node<?> node : getTo()) {
                        node.createRoute(multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            case "filter":
                if (!(config instanceof FilterConfig filterConfig)) {
                    throw new SigletError("");
                }
                if (getTo().size() == 1) {
                    getTo().getFirst().createRoute(routeCreator.addFilter(
                            new GroovyPredicate(filterConfig.getExpression().getValue(), GroovyPropertySetter.trace)));
                } else {
                    RouteCreator multicast = routeCreator.addFilter(
                                    new GroovyPredicate(filterConfig.getExpression().getValue(),
                                            GroovyPropertySetter.trace))
                            .startMulticast();
                    for (Node<?> node : getTo()) {
                        node.createRoute(multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            case "router":
                if (!(config instanceof RouterConfig routerConfig)) {
                    throw new SigletError("");
                }
                RouteCreator choice = routeCreator.startChoice();
                for (Route route : routerConfig.getRoutes().getValue()) {
                    getNodeFromName(route.getTo().getValue()).createRoute(choice.addChoice(
                            new GroovyPredicate(route.getExpression().getValue(), GroovyPropertySetter.trace)));
                }
                getNodeFromName(routerConfig.getDefaultRoute().getValue()).createRoute(choice.endChoice());
                break;
            default:
                throw new IllegalStateException("not yet implemented");
        }

    }

    public Node<?> getNodeFromName(String name) {
        for (Node<?> node : getTo()) {
            if (name.equals(node.getName())) {
                return node;
            }
        }
        throw new SigletError("Could not find node named " + name + " in to property!");
    }

}
