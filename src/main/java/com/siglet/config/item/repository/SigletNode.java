package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.SigletItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;
import com.siglet.pipeline.processor.common.action.ActionConfig;
import com.siglet.pipeline.processor.common.action.GroovyProcessor;
import com.siglet.pipeline.processor.common.filter.FilterConfig;
import com.siglet.pipeline.processor.common.filter.GroovyPredicate;
import com.siglet.pipeline.processor.common.router.Route;
import com.siglet.pipeline.processor.common.router.RouterConfig;
import com.siglet.pipeline.processor.traceaggregator.TraceAggregatorConfig;

import java.util.ArrayList;
import java.util.List;

public final class SigletNode extends Node<SigletItem> {

    private List<Node<?>> to = new ArrayList<>();

    private PipelineNode pipeline;

    SigletNode(SigletItem item, NodeRepository nodeRepository) {
        super(item, nodeRepository);
    }

    public List<Node<?>> getTo() {
        return to;
    }

    public void setTo(List<Node<?>> to) {
        this.to = to;
    }

    public PipelineNode getPipeline() {
        return pipeline;
    }

    public void setPipeline(PipelineNode pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void createRoute(RouteCreator routeCreator) {
        Object config = getItem().getConfig();
        switch (getItem().getType()) {
            case "groovy-action":
                if (!(config instanceof ActionConfig processorConfig)) {
                    throw new SigletError("Internal error! Spanlet has 'processor' type but item does not have " +
                            "ProcessorConfig config");
                }
                if (getTo().size() == 1) {
                    getTo().getFirst().createRoute(routeCreator
                            .addProcessor(new GroovyProcessor(processorConfig.getAction())));
                } else {
                    RouteCreator multicast = routeCreator.addProcessor(
                            new GroovyProcessor(processorConfig.getAction())).startMulticast();
                    for (Node<?> node : getTo()) {
                        node.createRoute(multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            case "groovy-filter":
                if (!(config instanceof FilterConfig filterConfig)) {
                    throw new SigletError("Internal error! Spanlet has 'filter' type but item does not have " +
                            "FilterConfig config");
                }
                if (getTo().size() == 1) {
                    getTo().getFirst().createRoute(routeCreator.addFilter(
                            new GroovyPredicate(filterConfig.getExpression())));
                } else {
                    RouteCreator multicast = routeCreator.addFilter(
                                    new GroovyPredicate(filterConfig.getExpression()))
                            .startMulticast();
                    for (Node<?> node : getTo()) {
                        node.createRoute(multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            case "groovy-router":
                if (!(config instanceof RouterConfig routerConfig)) {
                    throw new SigletError("Internal error! Spanlet has 'router' type but item does not have " +
                            "RouterConfig config");
                }
                RouteCreator choice = routeCreator.startChoice();
                for (Route route : routerConfig.getRoutes()) {
                    getNodeRepository().getNodeByName(route.getTo()).createRoute(choice.addChoice(
                            new GroovyPredicate(route.getWhen())));
                }
                getNodeRepository().getNodeByName(routerConfig.getDefaultRoute()).createRoute(choice.endChoice());
                break;
            case "default":
                if (!(config instanceof TraceAggregatorConfig traceAggregatorConfig)) {
                    throw new SigletError("Internal error! TraceAggregator does not have " +
                            "TraceAggregatorConfig config");
                }
                if (getTo().size() == 1) {

                    getTo().getFirst().createRoute(routeCreator.traceAggregator(traceAggregatorConfig.getCompletionExpression(),
                            traceAggregatorConfig.getInactiveTimeoutMillis(), traceAggregatorConfig.getTimeoutMillis()));
                } else {
                    RouteCreator multicast = routeCreator.traceAggregator( traceAggregatorConfig.getCompletionExpression(),
                            traceAggregatorConfig.getInactiveTimeoutMillis(), traceAggregatorConfig.getTimeoutMillis()).startMulticast();
                    for (Node<?> node : getTo()) {
                        node.createRoute(multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            default:
                throw new IllegalStateException("not yet implemented");
        }

    }
}
