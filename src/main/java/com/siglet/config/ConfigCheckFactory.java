package com.siglet.config;

import com.siglet.config.item.*;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.pipeline.metriclet.MetricletCheckerDiscriminator;
import com.siglet.pipeline.metriclet.MetricletTypes;
import com.siglet.pipeline.spanlet.span.SpanletCheckerDiscriminator;
import com.siglet.pipeline.spanlet.span.SpanletTypes;
import com.siglet.pipeline.spanlet.trace.TraceletCheckerDiscriminator;
import com.siglet.pipeline.spanlet.trace.TraceletTypes;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregatorCheckerDiscriminator;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregatorItem;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregatorTypes;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class ConfigCheckFactory {

    private ConfigCheckFactory() {
    }

    public static final String GRPC_PROP = "grpc";

    public static final String ADDRESS_PROP = "address";

    public static final String BATCH_SIZE_IN_SIGNAL_PROP = "batch-size-in-signals";

    public static final String BATCH_TIMEOUT_IN_MILLIS_PROP = "batch-timeout-in-millis";

    public static final String SIGNAL_TYPE_PROP = "signal-type";

    public static final String DEBUG_PROP = "debug";

    public static final String DROP_PROP = "drop";

    public static final String SPANLET_PROP = "spanlet";

    public static final String METRICLET_PROP = "metriclet";

    public static final String TO_PROP = "to";

    public static final String TYPE_PROP = "type";

    public static final String CONFIG_PROP = "config";

    public static final String TRACELET_PROP = "tracelet";

    public static final String TRACE_AGGREGATOR_PROP = "trace-aggregator";

    public static final String TRACE_PROP = "trace";

    public static final String METRIC_PROP = "metric";

    public static final String FROM_PROP = "from";

    public static final String START_PROP = "start";

    public static final String PIPELINE_PROP = "pipeline";

    public static final String RECEIVERS_PROP = "receivers";

    public static final String EXPORTERS_PROP = "exporters";

    public static final String PIPELINES_PROP = "pipelines";


    public static NodeChecker receiversChecker() {

        return array(alternative(grpcReceiverChecker(), debugReceiverChecker()));

    }

    public static NodeChecker grpcReceiverChecker() {
        return strictObject(GrpcReceiverItem::new,
                requiredProperty(GrpcReceiverItem::setName, GRPC_PROP, text()),
                requiredProperty(GrpcReceiverItem::setAddress, ADDRESS_PROP, text(inetSocketAddress())),
                property(GrpcReceiverItem::setSignalType, SIGNAL_TYPE_PROP, false, text())
        );
    }

    public static NodeChecker debugReceiverChecker() {
        return strictObject(DebugReceiverItem::new,
                requiredProperty(DebugReceiverItem::setName, DEBUG_PROP, text()),
                requiredProperty(DebugReceiverItem::setAddress, ADDRESS_PROP, text())
        );
    }

    public static NodeChecker grpcExportersChecker() {

        return array(alternative(grpcExporterChecker(), debugExporterChecker()));

    }

    public static NodeChecker grpcExporterChecker() {
        return strictObject(GrpcExporterItem::new,
                requiredProperty(GrpcExporterItem::setName, GRPC_PROP, text()),
                requiredProperty(GrpcExporterItem::setAddress, ADDRESS_PROP, text(inetSocketAddress())),
                optionalProperty(GrpcExporterItem::setBatchSizeInSignals, BATCH_SIZE_IN_SIGNAL_PROP, numberInt()),
                optionalProperty(GrpcExporterItem::setBatchTimeoutInMillis, BATCH_TIMEOUT_IN_MILLIS_PROP, numberInt()));
    }

    public static NodeChecker debugExporterChecker() {
        return strictObject(DebugExporterItem::new,
                requiredProperty(DebugExporterItem::setName, DEBUG_PROP, text()),
                requiredProperty(DebugExporterItem::setAddress, ADDRESS_PROP, text()));
    }

    public static NodeChecker metricletChecker() {

        return strictObject(MetricletItem::new,
                requiredProperty(MetricletItem::setName, METRICLET_PROP, text()),
                alternativeRequiredProperty(TO_PROP,
                        requiredProperty(MetricletItem::setTo, TO_PROP, array(text())),
                        requiredProperty(MetricletItem::setToSingleValue, TO_PROP, text())),
                requiredProperty(MetricletItem::setType, TYPE_PROP, text()),
                requiredDynamicProperty(CONFIG_PROP,
                        new MetricletCheckerDiscriminator(new MetricletTypes())));

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

    public static NodeChecker pipelinesChecker() {
        return array(pipelineChecker());
    }

    public static NodeChecker pipelineChecker() {
        return alternative(
                tracePipelineChecker(),
                metricPipelineChecker()
        );
    }

    public static NodeChecker tracePipelineChecker() {
        return strictObject(TracePipelineItem::new,
                requiredProperty(TracePipelineItem::setName, TRACE_PROP, text()),
                alternativeRequiredProperty(FROM_PROP,
                        requiredProperty(TracePipelineItem::setFrom, FROM_PROP, array(text())),
                        requiredProperty(TracePipelineItem::setFromSingleValue, FROM_PROP, text())),
                alternativeRequiredProperty(START_PROP,
                        requiredProperty(TracePipelineItem::setStart, START_PROP, array(text())),
                        requiredProperty(TracePipelineItem::setStartSingleValue, START_PROP, text())),
                requiredProperty(TracePipelineItem::setProcessors, PIPELINE_PROP,
                        array(spanletChecker())));
    }

    public static NodeChecker metricPipelineChecker() {
        return strictObject(MetricPipelineItem::new,
                requiredProperty(MetricPipelineItem::setName, METRIC_PROP, text()),
                alternativeRequiredProperty(FROM_PROP,
                        requiredProperty(MetricPipelineItem::setFrom, FROM_PROP, array(text())),
                        requiredProperty(MetricPipelineItem::setFromSingleValue, FROM_PROP, text())),
                alternativeRequiredProperty(START_PROP,
                        requiredProperty(MetricPipelineItem::setStart, START_PROP, array(text())),
                        requiredProperty(MetricPipelineItem::setStartSingleValue, START_PROP, text())),
                requiredProperty(MetricPipelineItem::setProcessors, PIPELINE_PROP,
                        array(metricletChecker())));
    }

    public static NodeChecker globalConfigChecker() {
        return strictObject(ConfigItem::new,
                requiredProperty(ConfigItem::setReceivers, RECEIVERS_PROP,
                        receiversChecker()),
                requiredProperty(ConfigItem::setExporters, EXPORTERS_PROP,
                        grpcExportersChecker()),
                requiredProperty(ConfigItem::setPipelines, PIPELINES_PROP,
                        pipelinesChecker()));
    }
}
