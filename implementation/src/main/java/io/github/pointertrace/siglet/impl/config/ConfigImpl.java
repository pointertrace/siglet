package io.github.pointertrace.siglet.impl.config;

import io.github.pointertrace.siglet.impl.config.descriptor.QueueSizeDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ThreadPoolSizeDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.Graph;
import io.github.pointertrace.siglet.impl.config.graph.GraphFactory;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;

public class ConfigImpl implements Config {

    private final YamlDescriptor yamlDescriptor;

    private final ReceiverTypeRegistry receiverTypeRegistry;

    private final ProcessorTypeRegistry processorTypeRegistry;

    private final ExporterTypeRegistry exporterTypeRegistry;

    private Graph graph;

    private final GraphFactory graphFactory = new GraphFactory();

    public ConfigImpl(YamlDescriptor yamlDescriptor, ReceiverTypeRegistry receiverTypeRegistry,
                      ProcessorTypeRegistry processorTypeRegistry, ExporterTypeRegistry exporterTypeRegistry) {
        this.yamlDescriptor = yamlDescriptor;
        this.receiverTypeRegistry = receiverTypeRegistry;
        this.processorTypeRegistry = processorTypeRegistry;
        this.exporterTypeRegistry = exporterTypeRegistry;
        this.graph = graphFactory.create(yamlDescriptor);
    }

    @Override
    public ProcessorTypeRegistry getProcessorTypeRegistry() {
        return processorTypeRegistry;
    }

    @Override
    public ReceiverTypeRegistry getReceiverTypeRegistry() {
        return receiverTypeRegistry;
    }

    @Override
    public ExporterTypeRegistry getExporterTypeRegistry() {
        return exporterTypeRegistry;
    }

    @Override
    public YamlDescriptor getYamlDescriptor() {
        return yamlDescriptor;
    }

    @Override
    public int getQueueSize(QueueSizeDescriptor queueSizeDescriptor) {
        String queueSize = null;
        if (queueSizeDescriptor instanceof Component component) {
            queueSize = System.getenv("SIGLET_" + component.getName() + "_QUEUE_SIZE");
        }
        if (queueSize != null) {
            return Integer.parseInt(queueSize);
        }
        if (queueSizeDescriptor.getQueueSize() != null) {
            return queueSizeDescriptor.getQueueSize().getValue().intValue();
        }
        if (yamlDescriptor.getGlobalConfig() != null && yamlDescriptor.getGlobalConfig().getQueueSize() != null) {
            return yamlDescriptor.getGlobalConfig().getQueueSize().getValue().intValue();
        }
        return 1000;
    }

    @Override
    public int getThreadPoolSize(ThreadPoolSizeDescriptor threadPoolSizeDescriptor) {
        String threadPoolSize = null;
        if (threadPoolSizeDescriptor instanceof Component component) {
            threadPoolSize = System.getenv("SIGLET_" + component.getName() + "_THREAD_POOL_SIZE");
        }
        if (threadPoolSize != null) {
            return Integer.parseInt(threadPoolSize);
        }
        if (threadPoolSizeDescriptor.getThreadPoolSize() != null) {
            return threadPoolSizeDescriptor.getThreadPoolSize().getValue().intValue();
        }
        if (yamlDescriptor.getGlobalConfig() != null && yamlDescriptor.getGlobalConfig().getThreadPoolSize() != null) {
            return yamlDescriptor.getGlobalConfig().getThreadPoolSize().getValue().intValue();
        }
        return 1000;
    }

    @Override
    public int getInternalMetricsExportIntervalMillis() {
        if (yamlDescriptor.getGlobalConfig() != null
                && yamlDescriptor.getGlobalConfig().getInternalMetricsExportIntervalMillis() != null) {
            return yamlDescriptor.getGlobalConfig().getInternalMetricsExportIntervalMillis().getValue().intValue();
        }
        return 1000;
    }
}
