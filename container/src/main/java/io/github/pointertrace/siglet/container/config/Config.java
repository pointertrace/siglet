package io.github.pointertrace.siglet.container.config;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.graph.Graph;
import io.github.pointertrace.siglet.container.config.graph.GraphFactory;
import io.github.pointertrace.siglet.container.config.raw.GlobalConfig;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfig;
import io.github.pointertrace.siglet.container.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.container.engine.receiver.ReceiverTypeRegistry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final Map<String, SigletDefinition> sigletRegistry;

    private final RawConfig rawConfig;

    private Graph graph;

    private final GraphFactory graphFactory = new GraphFactory();

    private final GlobalConfig globalConfig;

    private final ReceiverTypeRegistry receiverTypeRegistry;

    private final ProcessorTypeRegistry processorTypeRegistry;

    private final ExporterTypeRegistry exporterTypeRegistry;

    public Config(RawConfig rawConfig, List<SigletBundle> sigletBundles,ReceiverTypeRegistry receiverTypeRegistry,
                  ProcessorTypeRegistry processorTypeRegistry, ExporterTypeRegistry exporterTypeRegistry) {
        this.rawConfig = rawConfig;
        this.sigletRegistry = sigletBundles.stream()
                .flatMap(sb -> sb.definitions().stream())
                .collect(Collectors.toMap(sb -> sb.getSigletConfig().name(), Function.identity()));

        this.receiverTypeRegistry = receiverTypeRegistry;
        this.processorTypeRegistry = processorTypeRegistry;
        this.exporterTypeRegistry = exporterTypeRegistry;
        this.globalConfig = rawConfig.getGlobalConfig() == null ? new GlobalConfig() : rawConfig.getGlobalConfig();
    }

    public SigletConfig getSigletConfig(String name) {
        if (!sigletRegistry.containsKey(name)) {
            return sigletRegistry.get(name).getSigletConfig();
        } else {
            throw new SigletError(String.format("Could not find siglet named %s",name));
        }
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public ProcessorTypeRegistry getProcessorTypes() {
        return processorTypeRegistry;
    }

    public ReceiverTypeRegistry getReceiverTypeRegistry() {
        return receiverTypeRegistry;
    }

    public ExporterTypeRegistry getExporterTypeRegistry() {
        return exporterTypeRegistry;
    }

    public RawConfig getRawConfig() {
        return rawConfig;
    }

}
