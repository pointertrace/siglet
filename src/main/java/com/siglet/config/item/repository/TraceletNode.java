package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.TraceletItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;
import com.siglet.spanlet.GroovyPropertySetter;
import com.siglet.spanlet.filter.FilterConfig;
import com.siglet.spanlet.filter.GroovyPredicate;
import com.siglet.spanlet.processor.GroovyProcessor;
import com.siglet.spanlet.processor.ProcessorConfig;
import com.siglet.spanlet.router.Route;
import com.siglet.spanlet.router.RouterConfig;


public class TraceletNode extends ProcessorNode<TraceletItem> {

    public TraceletNode(String name, TraceletItem item) {
        super(name, item);
    }

    public void createRoute(RouteCreator routeCreator) {
        Object config = getItem().getConfig();
        switch (getItem().getType()) {
            case "processor":
                if (!(config instanceof ProcessorConfig processorConfig)) {
                    throw new SigletError("");
                }
                if (getTo().size() == 1) {
                    getTo().getFirst().createRoute(routeCreator.
                            addProcessor(new GroovyProcessor(processorConfig.getAction(), GroovyPropertySetter.trace)));
                } else {
                    RouteCreator multicast = routeCreator.addProcessor(
                            new GroovyProcessor(processorConfig.getAction(), GroovyPropertySetter.trace)).startMulticast();
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
                            new GroovyPredicate(filterConfig.getExpression(), GroovyPropertySetter.trace)));
                } else {
                    RouteCreator multicast = routeCreator.addFilter(
                                    new GroovyPredicate(filterConfig.getExpression(), GroovyPropertySetter.trace))
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
                for (Route route : routerConfig.getRoutes()) {
                    getNodeFromName(route.getTo()).createRoute(choice.addChoice(
                            new GroovyPredicate(route.getExpression(), GroovyPropertySetter.trace)));
                }
                getNodeFromName(routerConfig.getDefaultRoute()).createRoute(choice.endChoice());
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
