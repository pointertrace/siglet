package io.github.pointertrace.siglet.impl.config;

import io.github.pointertrace.siglet.impl.config.descriptor.QueueSizeDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ThreadPoolSizeDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;

public interface Config {

    ProcessorTypeRegistry getProcessorTypeRegistry();

    ReceiverTypeRegistry getReceiverTypeRegistry();

    ExporterTypeRegistry getExporterTypeRegistry();

    YamlDescriptor getYamlDescriptor();

    int getQueueSize(QueueSizeDescriptor threadPoolSizeDescriptor);

    int getThreadPoolSize(ThreadPoolSizeDescriptor threadPoolSizeDescriptor);

    int getInternalMetricsExportIntervalMillis();
}
