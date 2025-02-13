package com.siglet.pipeline.processor.traceaggregator;

import com.siglet.config.item.ProcessorItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.pipeline.common.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class TraceAggregationDefinition implements ConfigDefinition {

    @Override
    public NodeChecker getChecker() {
        return requiredProperty(ProcessorItem::setConfig, "config",
                strictObject(TraceAggregatorConfig::new,
                        property(TraceAggregatorConfig::setTimeoutMillis,
                                "timeout-millis", false, anyNumberChecker()),
                        property(TraceAggregatorConfig::setInactiveTimeoutMillis,
                                "inactive-timeout-millis", false,anyNumberChecker()),
                        property(TraceAggregatorConfig::setCompletionExpression,
                                "completion-expression", false, text())));
    }
}
