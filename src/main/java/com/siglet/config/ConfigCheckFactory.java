package com.siglet.config;

import com.siglet.config.item.*;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.span.SpanletCheckerDiscriminator;
import com.siglet.spanlet.span.SpanletTypes;
import com.siglet.spanlet.trace.TraceletCheckerDiscriminator;
import com.siglet.spanlet.trace.TraceletTypes;
import com.siglet.spanlet.traceaggregator.TraceAggregatorCheckerDiscriminator;
import com.siglet.spanlet.traceaggregator.TraceAggregatorItem;
import com.siglet.spanlet.traceaggregator.TraceAggregatorTypes;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class ConfigCheckFactory {
    public static final String GRPC_PROP = "grpc";
    public static final String ADDRESS_PROP = "address";
    public static final String DEBUG_PROP = "debug";
    public static final String SPANLET_PROP = "spanlet";
    public static final String TO_PROP = "to";
    public static final String TYPE_PROP = "type";
    public static final String CONFIG_PROP = "config";
    public static final String TRACELET_PROP = "tracelet";
    public static final String TRACE_AGGREGATOR_PROP = "trace-aggregator";
    public static final String TRACE_PROP = "trace";
    public static final String FROM_PROP = "from";
    public static final String START_PROP = "start";
    public static final String PIPELINE_PROP = "pipeline";
    public static final String RECEIVERS_PROP = "receivers";
    public static final String EXPORTERS_PROP = "exporters";
    public static final String PIPELINES_PROP = "pipelines";

    public static NodeChecker receiversChecker() {

        return array(
                alternative(
                        strictObject(GrpcReceiverItem::new,
                                requiredProperty(GrpcReceiverItem::setName, GRPC_PROP, text()),
                                requiredProperty(GrpcReceiverItem::setAddress, ADDRESS_PROP, text(inetSocketAddress()))
                        ),
                        strictObject(DebugReceiverItem::new,
                                requiredProperty(DebugReceiverItem::setName, DEBUG_PROP, text()),
                                requiredProperty(DebugReceiverItem::setAddress, ADDRESS_PROP, text())
                        )
                )
        );

    }

    public static NodeChecker grpcExportersChecker() {

        return array(
                alternative(
                        strictObject(GrpcExporterItem::new,
                                requiredProperty(GrpcExporterItem::setName, GRPC_PROP, text()),
                                requiredProperty(GrpcExporterItem::setAddress, ADDRESS_PROP, text(inetSocketAddress()))
                        ),
                        strictObject(DebugExporterItem::new,
                                requiredProperty(DebugExporterItem::setName, DEBUG_PROP, text()),
                                requiredProperty(DebugExporterItem::setAddress, ADDRESS_PROP, text()))
                )
        );
    }

    public static NodeChecker spanletChecker() {

        return alternative(
                strictObject(SpanletItem::new,
                        requiredProperty(SpanletItem::setName, SPANLET_PROP, text()),
                        alternativeRequiredProperty(TO_PROP,
                                requiredProperty(SpanletItem::setTo, TO_PROP, array(text())),
                                requiredProperty(SpanletItem::setToSingleValue, TO_PROP, text())),
                        requiredProperty(SpanletItem::setType, TYPE_PROP, text()),
                        requiredDynamicProperty(CONFIG_PROP,
                                new SpanletCheckerDiscriminator(new SpanletTypes()))),
                strictObject(TraceletItem::new,
                        requiredProperty(TraceletItem::setName, TRACELET_PROP, text()),
                        alternativeRequiredProperty(TO_PROP,
                                requiredProperty(TraceletItem::setTo, TO_PROP, array(text())),
                                requiredProperty(TraceletItem::setToSingleValue, TO_PROP, text())),
                        requiredProperty(TraceletItem::setType, TYPE_PROP, text()),
                        requiredDynamicProperty(CONFIG_PROP,
                                new TraceletCheckerDiscriminator(new TraceletTypes()))),
                strictObject(TraceAggregatorItem::new,
                        requiredProperty(TraceAggregatorItem::setName, TRACE_AGGREGATOR_PROP, text()),
                        alternativeRequiredProperty(TO_PROP,
                                requiredProperty(TraceAggregatorItem::setTo, TO_PROP, array(text())),
                                requiredProperty(TraceAggregatorItem::setToSingleValue, TO_PROP, text())),
                        property(TraceAggregatorItem::setType, TYPE_PROP, false, text()),
                        requiredDynamicProperty(CONFIG_PROP,
                                new TraceAggregatorCheckerDiscriminator(new TraceAggregatorTypes())))
        );
    }

    public static NodeChecker tracePipelineChecker() {
        return array(strictObject(TracePipelineItem::new,
                requiredProperty(TracePipelineItem::setName, TRACE_PROP, text()),
                alternativeRequiredProperty(FROM_PROP,
                        requiredProperty(TracePipelineItem::setFrom, FROM_PROP, array(text())),
                        requiredProperty(TracePipelineItem::setFromSingleValue, FROM_PROP, text())),
                alternativeRequiredProperty(START_PROP,
                        requiredProperty(TracePipelineItem::setStart, START_PROP, array(text())),
                        requiredProperty(TracePipelineItem::setStartSingleValue, START_PROP, text())),
                requiredProperty(TracePipelineItem::setProcessors, PIPELINE_PROP,
                        array(spanletChecker()))));
    }

    public static NodeChecker globalConfigChecker() {
        return strictObject(ConfigItem::new,
                requiredProperty(ConfigItem::setReceivers, RECEIVERS_PROP,
                        receiversChecker()),
                requiredProperty(ConfigItem::setExporters, EXPORTERS_PROP,
                        grpcExportersChecker()),
                requiredProperty(ConfigItem::setPipelines, PIPELINES_PROP,
                        tracePipelineChecker()));
    }
}
