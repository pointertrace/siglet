package com.siglet.config;

import com.siglet.config.item.*;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.pipeline.processor.ProcessorCheckerDiscriminator;
import com.siglet.pipeline.processor.ProcessorTypes;

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

    public static final String TO_PROP = "to";

    public static final String TYPE_PROP = "type";

    public static final String CONFIG_PROP = "config";

    private static final String NAME_PROP = "name";

    public static final String KIND_PROP = "kind";

    public static final String SIGNAL_PROP = "signal";

    public static final String SIGLETS_PROP = "siglets";

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
                requiredProperty(GrpcReceiverItem::setName, GrpcReceiverItem::setNameLocation,
                        GRPC_PROP, text()),
                requiredProperty(GrpcReceiverItem::setAddress, GrpcReceiverItem::setAddressLocation,
                        ADDRESS_PROP, text(inetSocketAddress())),
                property(GrpcReceiverItem::setSignal, GrpcReceiverItem::setSignalTypeLocation,
                        SIGNAL_PROP, false, text(new SignalTransformer()))
        );
    }

    public static NodeChecker debugReceiverChecker() {
        return strictObject(DebugReceiverItem::new,
                requiredProperty(DebugReceiverItem::setName, DebugReceiverItem::setNameLocation,
                        DEBUG_PROP, text()),
                requiredProperty(DebugReceiverItem::setAddress, DebugReceiverItem::setAddressLocation,
                        ADDRESS_PROP, text())
        );
    }

    public static NodeChecker grpcExportersChecker() {
        return array(alternative(grpcExporterChecker(), debugExporterChecker()));
    }

    public static NodeChecker grpcExporterChecker() {
        return strictObject(GrpcExporterItem::new,
                requiredProperty(GrpcExporterItem::setName, GrpcExporterItem::setNameLocation,
                        GRPC_PROP, text()),
                requiredProperty(GrpcExporterItem::setAddress, GrpcExporterItem::setAddressLocation,
                        ADDRESS_PROP, text(inetSocketAddress())),
                optionalProperty(GrpcExporterItem::setBatchSizeInSignals,
                        GrpcExporterItem::setBatchSizeInSignalsLocation, BATCH_SIZE_IN_SIGNAL_PROP,
                        numberInt()),
                optionalProperty(GrpcExporterItem::setBatchTimeoutInMillis,
                        GrpcExporterItem::setBatchTimeoutInMillisLocation,
                        BATCH_TIMEOUT_IN_MILLIS_PROP, numberInt()));
    }

    public static NodeChecker debugExporterChecker() {
        return strictObject(DebugExporterItem::new,
                requiredProperty(DebugExporterItem::setName, DebugExporterItem::setNameLocation,
                        DEBUG_PROP, text()),
                requiredProperty(DebugExporterItem::setAddress, DebugExporterItem::setAddressLocation,
                        ADDRESS_PROP, text()));
    }

    public static NodeChecker sigletChecker() {
        return strictObject(SigletItem::new,
                requiredProperty(SigletItem::setName, SigletItem::setNameLocation,
                        NAME_PROP, text()),
                requiredProperty(SigletItem::setKind, SigletItem::setKindLocation,
                        KIND_PROP, text(new SigletKindTransformer())),
                alternativeRequiredProperty(TO_PROP,
                        requiredProperty(SigletItem::setTo, SigletItem::setToLocation,
                                TO_PROP, array(text(new LocatedStringTransformer()))),
                        requiredProperty(SigletItem::setToSingleValue, SigletItem::setToLocation,
                                TO_PROP, text(new LocatedStringTransformer()))),
                requiredProperty(SigletItem::setType, SigletItem::setTypeLocation,
                        TYPE_PROP, text()),
                requiredDynamicProperty(CONFIG_PROP, SigletItem::setConfigLocation,
                        new ProcessorCheckerDiscriminator(new ProcessorTypes())));
    }


    public static NodeChecker pipelinesChecker() {
        return array(pipelineChecker());
    }

    public static NodeChecker pipelineChecker() {
        return strictObject(PipelineItem::new,
                requiredProperty(PipelineItem::setName, PipelineItem::setNameLocation,
                        NAME_PROP, text()),
                requiredProperty(PipelineItem::setSignal, PipelineItem::setSignalLocation,
                        SIGNAL_PROP, text(new SignalTransformer())),
                requiredProperty(PipelineItem::setFrom, PipelineItem::setFromLocation,
                        FROM_PROP, text()),
                alternativeRequiredProperty(START_PROP,
                        requiredProperty(PipelineItem::setStart, PipelineItem::setStartLocation,
                                START_PROP, array(text(new LocatedStringTransformer()))),
                        requiredProperty(PipelineItem::setStartSingleValue, PipelineItem::setStartLocation,
                                START_PROP, text(new LocatedStringTransformer()))
                ),
                requiredProperty(PipelineItem::setSiglets, PipelineItem::setSigletsLocation,
                        SIGLETS_PROP, array(sigletChecker())
                )
        );
    }

    public static NodeChecker globalConfigChecker() {
        return strictObject(ConfigItem::new,
                requiredProperty(ConfigItem::setReceivers,ConfigItem::setReceiversLocation, RECEIVERS_PROP,
                        receiversChecker()),
                requiredProperty(ConfigItem::setExporters,ConfigItem::setExportersLocation, EXPORTERS_PROP,
                        grpcExportersChecker()),
                requiredProperty(ConfigItem::setPipelines,ConfigItem::setPipelinesLocation, PIPELINES_PROP,
                        pipelinesChecker()));
    }
}
