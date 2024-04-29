package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.repository.routecreator.RouteCreator;
import com.siglet.spanlet.traceaggregator.TraceAggregatorConfig;
import com.siglet.spanlet.traceaggregator.TraceAggregatorItem;

public class TraceAggregatorNode extends ProcessorNode<TraceAggregatorItem> {

    public TraceAggregatorNode(String name, TraceAggregatorItem item) {
        super(name, item);
    }

    @Override
    public void createRoute(RouteCreator routeCreator) {
        switch (getItem().getType().getValue()) {
            case "default":
                Object config = getItem().getConfig();
                if (!(config instanceof TraceAggregatorConfig traceAggregatorConfig)) {
                       throw new SigletError("Internal error! TraceAggregator does not have " +
                            "TraceAggregatorConfig config");
                }
                if (getTo().size() == 1) {

                    getTo().getFirst().createRoute(routeCreator.traceAggregator(
                            traceAggregatorConfig.getCompletionExpression().getValue(),
                            traceAggregatorConfig.getInactiveTimeoutMillis().getValue(),
                            traceAggregatorConfig.getTimeoutMillis().getValue()));
                } else {
                    RouteCreator multicast = routeCreator.traceAggregator(
                            traceAggregatorConfig.getCompletionExpression().getValue(),
                            traceAggregatorConfig.getInactiveTimeoutMillis().getValue(),
                            traceAggregatorConfig.getTimeoutMillis().getValue()).startMulticast();
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
