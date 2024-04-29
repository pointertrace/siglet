package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.SpanletItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;
import com.siglet.spanlet.GroovyPropertySetter;
import com.siglet.spanlet.filter.FilterConfig;
import com.siglet.spanlet.filter.GroovyPredicate;
import com.siglet.spanlet.processor.GroovyProcessor;
import com.siglet.spanlet.processor.ProcessorConfig;
import com.siglet.spanlet.router.Route;
import com.siglet.spanlet.router.RouterConfig;

public class SpanletNode extends ProcessorNode<SpanletItem> {


    public SpanletNode(String name, SpanletItem item) {
        super(name, item);
    }

    @Override
    public void createRoute(RouteCreator routeCreator) {
        Object config = getItem().getConfig();
        switch (getItem().getType().getValue()) {
            case "processor":
                if (!(config instanceof ProcessorConfig processorConfig)) {
                    throw new SigletError("Internal error! Spanlet has 'processor' type but item does not have " +
                            "ProcessorConfig config");
                }
                if (getTo().size() == 1) {
                    getTo().getFirst().createRoute(routeCreator
                            .addProcessor(new GroovyProcessor(processorConfig.getAction().getValue(),
                                    GroovyPropertySetter.span)));
                } else {
                    RouteCreator multicast = routeCreator.addProcessor(
                            new GroovyProcessor(processorConfig.getAction().getValue(),
                                    GroovyPropertySetter.span)).startMulticast();
                    for (Node<?> node : getTo()) {
                        node.createRoute(multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            case "filter":
                if (!(config instanceof FilterConfig filterConfig)) {
                    throw new SigletError("Internal error! Spanlet has 'filter' type but item does not have " +
                            "FilterConfig config");
                }
                if (getTo().size() == 1) {
                    getTo().getFirst().createRoute(routeCreator.addFilter(
                            new GroovyPredicate(filterConfig.getExpression().getValue(),
                                    GroovyPropertySetter.span)));
                } else {
                    RouteCreator multicast = routeCreator.addFilter(
                                    new GroovyPredicate(filterConfig.getExpression().getValue(),
                                            GroovyPropertySetter.span))
                            .startMulticast();
                    for (Node<?> node : getTo()) {
                        node.createRoute(multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            case "router":
                if (!(config instanceof RouterConfig routerConfig)) {
                    throw new SigletError("Internal error! Spanlet has 'router' type but item does not have " +
                            "RouterConfig config");
                }
                RouteCreator choice = routeCreator.startChoice();
                for (Route route : routerConfig.getRoutes().getValue()) {
                    getNodeFromName(route.getTo().getValue()).createRoute(choice.addChoice(
                            new GroovyPredicate(route.getExpression().getValue(), GroovyPropertySetter.span)));
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

