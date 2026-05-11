package io.github.pointertrace.siglet.impl.config;

import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.Graph;
import io.github.pointertrace.siglet.impl.config.graph.GraphFactory;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final YamlDescriptor yamlDescriptor;

    private final ReceiverTypeRegistry receiverTypeRegistry;

    private final ProcessorTypeRegistry processorTypeRegistry;

    private final ExporterTypeRegistry exporterTypeRegistry;

    private Graph graph;

    private final GraphFactory graphFactory = new GraphFactory();

    public Config(YamlDescriptor yamlDescriptor, ReceiverTypeRegistry receiverTypeRegistry,
                  ProcessorTypeRegistry processorTypeRegistry, ExporterTypeRegistry exporterTypeRegistry) {
        this.yamlDescriptor = yamlDescriptor;
        this.receiverTypeRegistry = receiverTypeRegistry;
        this.processorTypeRegistry = processorTypeRegistry;
        this.exporterTypeRegistry = exporterTypeRegistry;
        this.graph = graphFactory.create(yamlDescriptor);
    }

    public ProcessorTypeRegistry getProcessorTypeRegistry() {
        return processorTypeRegistry;
    }

    public ReceiverTypeRegistry getReceiverTypeRegistry() {
        return receiverTypeRegistry;
    }

    public ExporterTypeRegistry getExporterTypeRegistry() {
        return exporterTypeRegistry;
    }

    public YamlDescriptor getYamlDescriptor() {
        return yamlDescriptor;
    }

    public int getQueueSize(ProcessorDescriptor processorDescriptor) {
        String queueSize = System.getenv("SIGLET_PROCESSOR_" + processorDescriptor.getName().getValue() + "_QUEUE_SIZE");
        if (queueSize != null) {
            return Integer.parseInt(queueSize);
        }
        if (processorDescriptor.getQueueSize() != null) {
            return processorDescriptor.getQueueSize().getValue().intValue();
        }
        if (yamlDescriptor.getGlobalConfig() != null && yamlDescriptor.getGlobalConfig().getQueueSize() != null) {
            return yamlDescriptor.getGlobalConfig().getQueueSize().getValue().intValue();
        }
        return 1000;
    }

    public int getThreadPoolSize(ProcessorDescriptor processorDescriptor) {
        String threadPoolSize = System.getenv("SIGLET_PROCESSOR_" + processorDescriptor.getName().getValue() + "_THREAD_POOL_SIZE");
        if (threadPoolSize != null) {
            return Integer.parseInt(threadPoolSize);
        }
        if (processorDescriptor.getThreadPoolSize() != null) {
            return processorDescriptor.getThreadPoolSize().getValue().intValue();
        }
        if (yamlDescriptor.getGlobalConfig() != null && yamlDescriptor.getGlobalConfig().getThreadPoolSize() != null) {
            return yamlDescriptor.getGlobalConfig().getThreadPoolSize().getValue().intValue();
        }
        return 1000;
    }
}
