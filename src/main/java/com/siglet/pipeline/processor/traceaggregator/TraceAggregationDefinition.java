package com.siglet.pipeline.processor.traceaggregator;

import com.siglet.config.item.SigletItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.pipeline.processor.common.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class TraceAggregationDefinition implements ConfigDefinition {

    // todo testar[[[
    @Override
    public NodeChecker getChecker() {
        return requiredProperty(SigletItem::setConfig, "config",
                strictObject(TraceAggregatorConfig::new,
                        property(TraceAggregatorConfig::setTimeoutMillis, TraceAggregatorConfig::setTimeoutMillisLocation,
                                "timeout-millis", false, anyNumberChecker()),
                        property(TraceAggregatorConfig::setInactiveTimeoutMillis, TraceAggregatorConfig::setInactiveTimeoutMillisLocation,
                                "inactive-timeout-millis", false,anyNumberChecker()),
                        property(TraceAggregatorConfig::setCompletionExpression, TraceAggregatorConfig::setCompletionExpressionLocation,
                                "completion-expression", false, text())));
    }
}
