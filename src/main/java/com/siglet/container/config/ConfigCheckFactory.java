package com.siglet.container.config;

import com.siglet.parser.NodeChecker;
import com.siglet.container.config.raw.*;
import com.siglet.container.engine.pipeline.processor.ProcessorCheckerDiscriminator;
import com.siglet.container.engine.pipeline.processor.ProcessorTypes;

import static com.siglet.parser.schema.SchemaFactory.*;

public class ConfigCheckFactory {


    private ConfigCheckFactory() {
    }

    public static final String GLOBAL_CONFIG_PROP = "global";

    public static final String QUEUE_SIZE_PROP = "queue-size";

    public static final String THREAD_POOL_SIZE_PROP = "thread-pool-size";

    public static final String SPAN_OBJECT_POOL_SIZE_PROP = "span-object-pool-size";

    public static final String METRIC_OBJECT_POOL_SIZE_PROP = "metric-object-pool-size";

    public static final String GRPC_PROP = "grpc";

    public static final String ADDRESS_PROP = "address";

    public static final String BATCH_SIZE_IN_SIGNAL_PROP = "batch-size-in-signals";

    public static final String BATCH_TIMEOUT_IN_MILLIS_PROP = "batch-timeout-in-millis";

    public static final String SIGNAL_TYPE_PROP = "signal-type";

    public static final String DEBUG_PROP = "debug";

    public static final String TO_PROP = "to";

    public static final String TYPE_PROP = "type";

    public static final String CONFIG_PROP = "config";

    private static final String NAME_PROP = "name";

    public static final String KIND_PROP = "kind";

    public static final String SIGNAL_PROP = "signal";

    public static final String PROCESSORS_PROP = "processors";

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
        return strictObject(GrpcReceiverConfig::new,
                requiredProperty(GrpcReceiverConfig::setName, GrpcReceiverConfig::setNameLocation,
                        GRPC_PROP, text()),
                requiredProperty(GrpcReceiverConfig::setAddress, GrpcReceiverConfig::setAddressLocation,
                        ADDRESS_PROP, text(inetSocketAddress())),
                property(GrpcReceiverConfig::setSignal, GrpcReceiverConfig::setSignalTypeLocation,
                        SIGNAL_PROP, false, text(new SignalTransformer()))
        );
    }

    public static NodeChecker debugReceiverChecker() {
        return strictObject(DebugReceiverConfig::new,
                requiredProperty(DebugReceiverConfig::setName, DebugReceiverConfig::setNameLocation, DEBUG_PROP, text())
        );
    }

    public static NodeChecker grpcExportersChecker() {
        return array(alternative(grpcExporterChecker(), debugExporterChecker()));
    }

    public static NodeChecker grpcExporterChecker() {
        return strictObject(GrpcExporterConfig::new,
                requiredProperty(GrpcExporterConfig::setName, GrpcExporterConfig::setNameLocation,
                        GRPC_PROP, text()),
                requiredProperty(GrpcExporterConfig::setAddress, GrpcExporterConfig::setAddressLocation,
                        ADDRESS_PROP, text(inetSocketAddress())),
                optionalProperty(GrpcExporterConfig::setBatchSizeInSignals,
                        GrpcExporterConfig::setBatchSizeInSignalsLocation, BATCH_SIZE_IN_SIGNAL_PROP,
                        numberInt()),
                optionalProperty(GrpcExporterConfig::setBatchTimeoutInMillis,
                        GrpcExporterConfig::setBatchTimeoutInMillisLocation,
                        BATCH_TIMEOUT_IN_MILLIS_PROP, numberInt()));
    }

    public static NodeChecker globalConfigChecker() {
        return strictObject(GlobalConfig::new,
                optionalProperty(GlobalConfig::setQueueSize, GlobalConfig::setQueueSizeLocation,
                        QUEUE_SIZE_PROP, numberInt()),
                optionalProperty(GlobalConfig::setThreadPoolSize, GlobalConfig::setThreadPoolSizeLocation,
                        THREAD_POOL_SIZE_PROP, numberInt()),
                optionalProperty(GlobalConfig::setSpanObjectPoolSize,
                        GlobalConfig::setSpanObjectPoolSizeLocation, SPAN_OBJECT_POOL_SIZE_PROP,
                        numberInt()),
                optionalProperty(GlobalConfig::setMetricObjectPoolSize,
                        GlobalConfig::setMetricObjectPoolSizeLocation, METRIC_OBJECT_POOL_SIZE_PROP,
                        numberInt()));
    }

    public static NodeChecker debugExporterChecker() {
        return strictObject(DebugExporterConfig::new,
                requiredProperty(DebugExporterConfig::setName, DebugExporterConfig::setNameLocation,
                        DEBUG_PROP, text())
        );
    }

    public static NodeChecker processorChecker(ProcessorTypes processorTypes) {
        return strictObject(ProcessorConfig::new,
                requiredProperty(ProcessorConfig::setName, ProcessorConfig::setNameLocation,
                        NAME_PROP, text()),
                requiredProperty(ProcessorConfig::setKind, ProcessorConfig::setKindLocation,
                        KIND_PROP, text(new ProcessorKindTransformer())),
                alternativeRequiredProperty(TO_PROP,
                        requiredProperty(ProcessorConfig::setTo, ProcessorConfig::setToLocation,
                                TO_PROP, array(text(new LocatedStringTransformer()))),
                        requiredProperty(ProcessorConfig::setToSingleValue, ProcessorConfig::setToLocation,
                                TO_PROP, text(new LocatedStringTransformer()))),
                requiredProperty(ProcessorConfig::setType, ProcessorConfig::setTypeLocation,
                        TYPE_PROP, text()),
                requiredDynamicProperty(CONFIG_PROP, ProcessorConfig::setConfigLocation,
                        new ProcessorCheckerDiscriminator(processorTypes)),
                optionalProperty(ProcessorConfig::setQueueSize, ProcessorConfig::setQueueSizeLocation,
                        QUEUE_SIZE_PROP, numberInt()),
                optionalProperty(ProcessorConfig::setThreadPoolSize, ProcessorConfig::setThreadPoolSizeLocation,
                        THREAD_POOL_SIZE_PROP, numberInt()));
    }


    public static NodeChecker pipelinesChecker(ProcessorTypes processorTypes) {
        return array(pipelineChecker(processorTypes));
    }

    public static NodeChecker pipelineChecker(ProcessorTypes processorTypes) {
        return strictObject(PipelineConfig::new,
                requiredProperty(PipelineConfig::setName, PipelineConfig::setNameLocation,
                        NAME_PROP, text()),
                requiredProperty(PipelineConfig::setSignal, PipelineConfig::setSignalLocation,
                        SIGNAL_PROP, text(new SignalTransformer())),
                requiredProperty(PipelineConfig::setFrom, PipelineConfig::setFromLocation,
                        FROM_PROP, text()),
                alternativeRequiredProperty(START_PROP,
                        requiredProperty(PipelineConfig::setStart, PipelineConfig::setStartLocation,
                                START_PROP, array(text(new LocatedStringTransformer()))),
                        requiredProperty(PipelineConfig::setStartSingleValue, PipelineConfig::setStartLocation,
                                START_PROP, text(new LocatedStringTransformer()))
                ),
                requiredProperty(PipelineConfig::setProcessors, PipelineConfig::setSigletsLocation,
                        PROCESSORS_PROP, array(processorChecker(processorTypes))
                )
        );
    }

    public static NodeChecker rawConfigChecker(ProcessorTypes processorTypes) {
        return strictObject(RawConfig::new,
                optionalProperty(RawConfig::setGlobalConfig, RawConfig::setGlobalConfigLocation, GLOBAL_CONFIG_PROP,
                        globalConfigChecker()),
                requiredProperty(RawConfig::setReceivers, RawConfig::setReceiversLocation, RECEIVERS_PROP,
                        receiversChecker()),
                requiredProperty(RawConfig::setExporters, RawConfig::setExportersLocation, EXPORTERS_PROP,
                        grpcExportersChecker()),
                requiredProperty(RawConfig::setPipelines, RawConfig::setPipelinesLocation, PIPELINES_PROP,
                        pipelinesChecker(processorTypes)));
    }
}
