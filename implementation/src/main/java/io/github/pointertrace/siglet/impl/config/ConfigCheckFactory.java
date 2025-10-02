package io.github.pointertrace.siglet.impl.config;

import io.github.pointertrace.siglet.impl.config.raw.*;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterCheckerDiscriminator;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorCheckerDiscriminator;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverCheckerDiscriminator;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.parser.NodeChecker;

import java.util.Set;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.*;

public class ConfigCheckFactory {


    private ConfigCheckFactory() {
    }

    public static final String GLOBAL_CONFIG_PROP = "global";

    public static final String QUEUE_SIZE_PROP = "queue-size";

    public static final String THREAD_POOL_SIZE_PROP = "thread-pool-size";

    public static final String SPAN_OBJECT_POOL_SIZE_PROP = "span-object-pool-size";

    public static final String METRIC_OBJECT_POOL_SIZE_PROP = "metric-object-pool-size";

    public static final String TO_PROP = "to";

    public static final String CONFIG_PROP = "config";

    private static final String NAME_PROP = "name";

    public static final String PROCESSORS_PROP = "processors";

    public static final String FROM_PROP = "from";

    public static final String START_PROP = "start";

    public static final String RECEIVERS_PROP = "receivers";

    public static final String EXPORTERS_PROP = "exporters";

    public static final String PIPELINES_PROP = "pipelines";




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

    public static Set<String> getExporterProperties() {
        return Set.of(CONFIG_PROP);
    }

    public static NodeChecker exportersChecker(ExporterTypeRegistry exporterTypeRegistry) {
        return array(exporterChecker(exporterTypeRegistry));
    }

    public static NodeChecker exporterChecker(ExporterTypeRegistry exporterTypeRegistry) {
        return strictObject(ExporterConfig::new,
                optionalDynamicKeyProperty(ExporterConfig::setType, ExporterConfig::setTypeLocation,
                        ExporterConfig::setName, ExporterConfig::setNameLocation,
                        exporterTypeRegistry.getExporterTypesNames(), text()),
                requiredDynamicProperty(CONFIG_PROP, ExporterConfig::setConfigLocation,
                        new ExporterCheckerDiscriminator(exporterTypeRegistry)));
    }

    public static NodeChecker receiversChecker(ReceiverTypeRegistry receiverTypeRegistry) {
        return array(receiverChecker(receiverTypeRegistry));
    }

    public static Set<String> getReceiverProperties() {
        return Set.of(CONFIG_PROP);
    }

    public static NodeChecker receiverChecker(ReceiverTypeRegistry receiverTypeRegistry) {
        return strictObject(ReceiverConfig::new,
                optionalDynamicKeyProperty(ReceiverConfig::setType, ReceiverConfig::setTypeLocation,
                        ReceiverConfig::setName, ReceiverConfig::setNameLocation,
                        receiverTypeRegistry.getReceiverTypesNames(), text()),
                requiredDynamicProperty(CONFIG_PROP, ReceiverConfig::setConfigLocation,
                        new ReceiverCheckerDiscriminator(receiverTypeRegistry)));
    }

    public static Set<String> getProcessorProperties() {
        return Set.of(TO_PROP, CONFIG_PROP, QUEUE_SIZE_PROP, THREAD_POOL_SIZE_PROP);
    }

    public static NodeChecker processorChecker(ProcessorTypeRegistry processorTypeRegistry) {
        return strictObject(ProcessorConfig::new,
                optionalDynamicKeyProperty(ProcessorConfig::setType, ProcessorConfig::setTypeLocation,
                        ProcessorConfig::setName, ProcessorConfig::setNameLocation,
                        processorTypeRegistry.getProcessorTypesNames(), text()),
                alternativeRequiredProperty(TO_PROP,
                        requiredProperty(ProcessorConfig::setTo, ProcessorConfig::setToLocation,
                                TO_PROP, array(text(new ProcessorDestinationTransformer()))),
                        requiredProperty(ProcessorConfig::setToSingleValue, ProcessorConfig::setToLocation,
                                TO_PROP, text(new ProcessorDestinationTransformer()))),
                requiredDynamicProperty(CONFIG_PROP, ProcessorConfig::setConfigLocation,
                        new ProcessorCheckerDiscriminator(processorTypeRegistry)),
                optionalProperty(ProcessorConfig::setQueueSize, ProcessorConfig::setQueueSizeLocation,
                        QUEUE_SIZE_PROP, numberInt()),
                optionalProperty(ProcessorConfig::setThreadPoolSize, ProcessorConfig::setThreadPoolSizeLocation,
                        THREAD_POOL_SIZE_PROP, numberInt()));
    }


    public static NodeChecker pipelinesChecker(ProcessorTypeRegistry processorTypeRegistry) {
        return array(pipelineChecker(processorTypeRegistry));
    }

    public static NodeChecker pipelineChecker(ProcessorTypeRegistry processorTypeRegistry) {
        return strictObject(PipelineConfig::new,
                requiredProperty(PipelineConfig::setName, PipelineConfig::setNameLocation,
                        NAME_PROP, text()),
                optionalProperty(PipelineConfig::setFrom, PipelineConfig::setFromLocation,
                        FROM_PROP, text()),
                alternativeRequiredProperty(START_PROP,
                        requiredProperty(PipelineConfig::setStart, PipelineConfig::setStartLocation,
                                START_PROP, array(text(new LocatedStringTransformer()))),
                        requiredProperty(PipelineConfig::setStartSingleValue, PipelineConfig::setStartLocation,
                                START_PROP, text(new LocatedStringTransformer()))
                ),
                optionalProperty(PipelineConfig::setProcessors, PipelineConfig::setSigletsLocation,
                        PROCESSORS_PROP, array(processorChecker(processorTypeRegistry))
                )
        );
    }

    public static NodeChecker rawConfigChecker(ReceiverTypeRegistry receiverTypeRegistry,
                                               ProcessorTypeRegistry processorTypeRegistry,
                                               ExporterTypeRegistry exporterTypeRegistry) {
        return strictObject(RawConfig::new,
                optionalProperty(RawConfig::setGlobalConfig, RawConfig::setGlobalConfigLocation, GLOBAL_CONFIG_PROP,
                        globalConfigChecker()),
                requiredProperty(RawConfig::setReceivers, RawConfig::setReceiversLocation, RECEIVERS_PROP,
                        receiversChecker(receiverTypeRegistry)),
                optionalProperty(RawConfig::setExporters, RawConfig::setExportersLocation, EXPORTERS_PROP,
                        exportersChecker(exporterTypeRegistry)),
                requiredProperty(RawConfig::setPipelines, RawConfig::setPipelinesLocation, PIPELINES_PROP,
                        pipelinesChecker(processorTypeRegistry)));
    }
}
