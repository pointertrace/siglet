package com.siglet.container.config.raw;

import com.siglet.SigletError;
import com.siglet.container.config.graph.Graph;
import com.siglet.container.engine.Context;
import com.siglet.parser.located.Location;
import com.siglet.utils.Joining;
import com.siglet.utils.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RawConfig extends BaseConfig {

    private static final EventLoopConfig defaultEventLoopConfig =
            EventLoopConfig.of(1_000, Runtime.getRuntime().availableProcessors());


    private GlobalConfig globalConfig;

    private Location globalConfigLocation;

    private List<ReceiverConfig> receivers;

    private Location receiversLocation;

    private List<ExporterConfig> exporters;

    private Location exportersLocation;

    private List<PipelineConfig> pipelines;

    private Location pipelinesLocation;

    public List<ReceiverConfig> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<ReceiverConfig> receiverConfigs) {
        this.receivers = receiverConfigs;
    }

    public List<ExporterConfig> getExporters() {
        return exporters;
    }

    public void setExporters(List<ExporterConfig> exporterConfigs) {
        this.exporters = exporterConfigs;
    }

    public List<PipelineConfig> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<PipelineConfig> pipelines) {
        this.pipelines = pipelines;
    }

    // todo chamar
    protected void validateUniqueNames() {
        String notUniqueNames = Stream.of(
                        getReceivers().stream()
                                .map(ReceiverConfig::getName),
                        getExporters().stream()
                                .map(ExporterConfig::getName),
                        getPipelines().stream()
                                .map(PipelineConfig::getName),
                        getPipelines().stream()
                                .flatMap(p -> p.getProcessors().stream())
                                .map(ProcessorConfig::getName)
                )
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(e -> "'" + e.getKey() + "' appears " + StringUtils.frequency(e.getValue()))
                .collect(Joining.twoDelimiters(", ", " and ",
                        "Names must be unique within the configuration file but: ", "!"));

        if (notUniqueNames != null) {
            throw new SigletError(notUniqueNames);
        }

    }

    public Graph createGraph(Context context) {
        Graph graph = new Graph();


        getReceivers().forEach(graph::addItem);
        getExporters().forEach(graph::addItem);
        getPipelines().forEach(graph::addItem);
        getPipelines().stream()
                .flatMap(this::getItems)
                .forEach(graph::addItem);

        graph.connect(context);

        return graph;
    }


    public Stream<ProcessorConfig> getItems(PipelineConfig pipeline) {
        return pipeline.getProcessors().stream();
    }

    @Override
    public void afterSetValues() {
        getReceivers().forEach(BaseConfig::afterSetValues);
        getExporters().forEach(BaseConfig::afterSetValues);
        getPipelines().forEach(BaseConfig::afterSetValues);
        getPipelines().stream()
                .flatMap(this::getItems)
                .forEach(BaseConfig::afterSetValues);
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe()); sb.append("  RawConfig:"); if (globalConfig != null) { sb.append("\n"); sb.append(globalConfig.describe(level + 1));
        }

        sb.append("\n");
        sb.append(prefix(level + 1));
        sb.append(receiversLocation.describe());
        sb.append("  receivers:");
        sb.append("\n");
        for (ReceiverConfig receiver : receivers) {
            sb.append(receiver.describe(level + 2));
        }

        sb.append(prefix(level + 1));
        sb.append(exportersLocation.describe());
        sb.append("  exporters:");
        sb.append("\n");
        for (ExporterConfig exporter : exporters) {
            sb.append(exporter.describe(level + 2));
        }

        sb.append(prefix(level + 1));
        sb.append(pipelinesLocation.describe());
        sb.append("  pipelines:");
        sb.append("\n");
        for (PipelineConfig pipeline : pipelines) {
            sb.append(pipeline.describe(level + 2));
        }

        return sb.toString();
    }

    public Location getReceiversLocation() {
        return receiversLocation;
    }

    public void setReceiversLocation(Location receiversLocation) {
        this.receiversLocation = receiversLocation;
    }

    public Location getExportersLocation() {
        return exportersLocation;
    }

    public void setExportersLocation(Location exportersLocation) {
        this.exportersLocation = exportersLocation;
    }

    public Location getPipelinesLocation() {
        return pipelinesLocation;
    }

    public void setPipelinesLocation(Location pipelinesLocation) {
        this.pipelinesLocation = pipelinesLocation;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public Location getGlobalConfigLocation() {
        return globalConfigLocation;
    }

    public void setGlobalConfigLocation(Location globalConfigLocation) {
        this.globalConfigLocation = globalConfigLocation;
    }
}
