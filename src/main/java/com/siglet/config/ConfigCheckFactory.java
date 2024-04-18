package com.siglet.config;

import com.siglet.config.item.*;
import com.siglet.config.item.GrpcReceiverItem;
import com.siglet.config.item.SpanletItem;
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


    public static NodeChecker receiversChecker() {

        return array(
                alternative(
                        strictObject(GrpcReceiverItem::new,
                                requiredProperty(GrpcReceiverItem::setName, "grpc", text()),
                                requiredProperty(GrpcReceiverItem::setAddress, "address", text(inetSocketAddress()))
                        ),
                        strictObject(DebugReceiverItem::new,
                                requiredProperty(DebugReceiverItem::setName, "debug", text()),
                                requiredProperty(DebugReceiverItem::setAddress, "address", text())
                        )
                )
        );

    }

    public static NodeChecker grpcExportersChecker() {

        return array(
                alternative(
                        strictObject(GrpcExporterItem::new,
                                requiredProperty(GrpcExporterItem::setName, "grpc", text()),
                                requiredProperty(GrpcExporterItem::setAddress, "address", text(inetSocketAddress()))
                        ),
                        strictObject(DebugExporterItem::new,
                                requiredProperty(DebugExporterItem::setName, "debug", text()),
                                requiredProperty(DebugExporterItem::setAddress, "address", text()))
                )
        );
    }

    public static NodeChecker spanletChecker() {

        return alternative(
                strictObject(SpanletItem::new,
                        requiredProperty(SpanletItem::setName, "spanlet", text()),
                        alternativeRequiredProperty("to",
                                requiredProperty(SpanletItem::setTo, "to", array(text())),
                                requiredProperty(SpanletItem::setToSingleValue, "to", text())),
                        requiredProperty(SpanletItem::setType, "type", text()),
                        requiredDynamicProperty("config",
                                new SpanletCheckerDiscriminator(new SpanletTypes()))),
                strictObject(TraceletItem::new,
                        requiredProperty(TraceletItem::setName, "tracelet", text()),
                        alternativeRequiredProperty("to",
                                requiredProperty(TraceletItem::setTo, "to", array(text())),
                                requiredProperty(TraceletItem::setToSingleValue, "to", text())),
                        requiredProperty(TraceletItem::setType, "type", text()),
                        requiredDynamicProperty("config",
                                new TraceletCheckerDiscriminator(new TraceletTypes()))),
                strictObject(TraceAggregatorItem::new,
                        requiredProperty(TraceAggregatorItem::setName, "trace-aggregator", text()),
                        alternativeRequiredProperty("to",
                                requiredProperty(TraceAggregatorItem::setTo, "to", array(text())),
                                requiredProperty(TraceAggregatorItem::setToSingleValue, "to", text())),
                        property(TraceAggregatorItem::setType, "type", false, text()),
                        requiredDynamicProperty("config",
                                new TraceAggregatorCheckerDiscriminator(new TraceAggregatorTypes())))
        );
    }

    public static NodeChecker tracePipelineChecker() {
        return array(strictObject(TracePipelineItem::new,
                requiredProperty(TracePipelineItem::setName, "trace", text()),
                alternativeRequiredProperty("from",
                        requiredProperty(TracePipelineItem::setFrom, "from", array(text())),
                        requiredProperty(TracePipelineItem::setFromSingleValue, "from", text())),
                alternativeRequiredProperty("start",
                        requiredProperty(TracePipelineItem::setStart, "start", array(text())),
                        requiredProperty(TracePipelineItem::setStartSingleValue, "start", text())),
                requiredProperty(TracePipelineItem::setProcessors, "pipeline",
                        array(spanletChecker()))));
    }

    public static NodeChecker globalConfigChecker() {
        return strictObject(ConfigItem::new,
                requiredProperty(ConfigItem::setReceivers, "receivers",
                        receiversChecker()),
                requiredProperty(ConfigItem::setExporters, "exporters",
                        grpcExportersChecker()),
                requiredProperty(ConfigItem::setPipelines, "pipelines",
                        tracePipelineChecker()));
    }
}
