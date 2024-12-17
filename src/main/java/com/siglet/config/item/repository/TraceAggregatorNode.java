package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.ValueItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregatorConfig;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregatorItem;

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
                            getValue(traceAggregatorConfig.getCompletionExpression()),
                            getValue(traceAggregatorConfig.getInactiveTimeoutMillis()),
                            getValue(traceAggregatorConfig.getTimeoutMillis())));
                } else {
                    // TODO ver se precisa de getValue!!!!!
                    RouteCreator multicast = routeCreator.traceAggregator(
                            getValue(traceAggregatorConfig.getCompletionExpression()),
                            getValue(traceAggregatorConfig.getInactiveTimeoutMillis()),
                            getValue(traceAggregatorConfig.getTimeoutMillis())).startMulticast();
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

    private <T> T getValue(ValueItem<T> valueItem) {
        return valueItem == null? null: valueItem.getValue();
    }

}
