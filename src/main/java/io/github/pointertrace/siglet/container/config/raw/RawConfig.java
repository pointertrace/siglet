package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siget.parser.Describable;
import io.github.pointertrace.siget.parser.located.Location;
import io.github.pointertrace.siglet.container.config.raw.validator.ComposedValidator;

import java.util.ArrayList;
import java.util.List;

public class RawConfig extends BaseConfig {

    private GlobalConfig globalConfig;

    private Location globalConfigLocation;

    private List<ReceiverConfig> receivers;

    private Location receiversLocation;

    private List<ExporterConfig> exporters = new ArrayList<>();

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


    @Override
    public void afterSetValues() {
        getReceivers().forEach(BaseConfig::afterSetValues);
        getExporters().forEach(BaseConfig::afterSetValues);
        getPipelines().forEach(BaseConfig::afterSetValues);
        getPipelines().stream()
                .flatMap(pipeline -> pipeline.getProcessors().stream())
                .forEach(BaseConfig::afterSetValues);

        getPipelines().stream()
                .flatMap(pipeline -> pipeline.getProcessors().stream())
                .forEach(processorConfig -> processorConfig.setRawConfig(this));

        getExporters().stream()
                .filter(GrpcExporterConfig.class::isInstance)
                .map(GrpcExporterConfig.class::cast)
                .forEach(exporterConfig -> exporterConfig.setRawConfig(this));

    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getLocation().describe());
        sb.append("  RawConfig:");
        if (globalConfig != null) {
            sb.append("\n");
            sb.append(globalConfig.describe(level + 1));
        }

        sb.append("\n");
        sb.append(Describable.prefix(level + 1));
        sb.append(receiversLocation.describe());
        sb.append("  receivers:");
        sb.append("\n");
        for (ReceiverConfig receiver : receivers) {
            sb.append(receiver.describe(level + 2));
        }

        sb.append(Describable.prefix(level + 1));
        sb.append(exportersLocation.describe());
        sb.append("  exporters:");
        sb.append("\n");
        for (ExporterConfig exporter : exporters) {
            sb.append(exporter.describe(level + 2));
        }

        sb.append(Describable.prefix(level + 1));
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

    public QueueSizeConfig getGlobalConfigQueueSize() {
        if (getGlobalConfig() == null) {
            return QueueSizeConfig.defaultConfig();
        } else {
            return QueueSizeConfig.defaultConfig().chain(QueueSizeConfig.of(getGlobalConfig()));
        }
    }

    public ThreadPoolSizeConfig getGlobalConfigThreadPoolSize() {
        if (getGlobalConfig() == null) {
            return ThreadPoolSizeConfig.defaultConfig();
        } else {
            return ThreadPoolSizeConfig.defaultConfig().chain(ThreadPoolSizeConfig.of(getGlobalConfig()));
        }
    }
}
