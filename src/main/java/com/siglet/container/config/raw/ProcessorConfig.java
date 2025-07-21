package com.siglet.container.config.raw;


import com.siglet.SigletError;
import com.siglet.parser.Describable;
import com.siglet.parser.located.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessorConfig extends BaseConfig implements QueueSizeConfig, ThreadPoolSizeConfig {

    private RawConfig rawConfig;

    private ProcessorKind processorKind;

    private Location kindLocation;

    private final List<LocatedString> to = new ArrayList<>();

    private Location toLocation;

    private String type;

    private Location typeLocation;

    private Object config;

    private Location configLocation;

    private String pipeline;

    private Integer queueSize;

    private Location queueSizeLocation;

    private Integer threadPoolSize;

    private Location threadPoolSizeLocation;

    public void setRawConfig(RawConfig rawConfig) {
        this.rawConfig = rawConfig;
    }

    public ProcessorKind getProcessorKind() {
        return processorKind;
    }

    public void setProcessorKind(ProcessorKind processorKind) {
        this.processorKind = processorKind;
    }

    public Location getKindLocation() {
        return kindLocation;
    }

    public void setKindLocation(Location kindLocation) {
        this.kindLocation = kindLocation;
    }

    public List<LocatedString> getTo() {
        return Collections.unmodifiableList(to);
    }

    // TODO test
    public List<String> getToNames() {
        return to.stream().map(LocatedString::getValue).toList();
    }

    public void setTo(List<LocatedString> to) {
        this.to.addAll(to);
    }

    public void setToSingleValue(LocatedString to) {
        this.to.add(to);
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Location getTypeLocation() {
        return typeLocation;
    }

    public void setTypeLocation(Location typeLocation) {
        this.typeLocation = typeLocation;
    }

    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public Location getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(Location configLocation) {
        this.configLocation = configLocation;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe());
        sb.append("  processorConfig:\n");


        sb.append(super.describe(level + 1));

        sb.append(prefix(level + 1));
        sb.append(kindLocation.describe());
        sb.append("  kind: ");
        sb.append(getProcessorKind());
        sb.append("\n");

        sb.append(prefix(level + 1));
        sb.append(toLocation.describe());
        sb.append("  to:\n");
        for (LocatedString toName : getTo()) {
            sb.append(prefix(level + 2));
            sb.append(toName.getLocation().describe());
            sb.append("  ");
            sb.append(toName.getValue());
            sb.append("\n");
        }

        sb.append(prefix(level + 1));
        sb.append(getTypeLocation().describe());
        sb.append("  type: ");
        sb.append(getType());

        if (queueSize != null) {
            sb.append("\n");
            sb.append(prefix(level + 1));
            sb.append(getQueueSizeLocation().describe());
            sb.append("  queueSize: ");
            sb.append(getQueueSize());
        }

        if (threadPoolSize != null) {
            sb.append("\n");
            sb.append(prefix(level + 1));
            sb.append(getThreadPoolSizeLocation().describe());
            sb.append("  threadPoolSize: ");
            sb.append(getThreadPoolSize());
        }
        sb.append("\n");
        sb.append(prefix(level + 1));
        sb.append(getConfigLocation().describe());

        if (getConfig() == null) {
            sb.append("  config: null");
        } else {
            sb.append(" config:\n");
            if (getConfig() instanceof Describable describableConfig) {
                sb.append(describableConfig.describe(level + 2));
            } else {
                sb.append(getConfig().toString());
            }
        }
        return sb.toString();
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public Location getQueueSizeLocation() {
        return queueSizeLocation;
    }

    public void setQueueSizeLocation(Location queueSizeLocation) {
        this.queueSizeLocation = queueSizeLocation;
    }

    public Location getThreadPoolSizeLocation() {
        return threadPoolSizeLocation;
    }

    public void setThreadPoolSizeLocation(Location threadPoolSizeLocation) {
        this.threadPoolSizeLocation = threadPoolSizeLocation;
    }

    public QueueSizeConfig getQueueSizeConfig() {
        if (rawConfig == null) {
            throw new SigletError("rawConfig is null");
        }
        return rawConfig.getGlobalConfigQueueSize().chain(QueueSizeConfig.of(this));
    }

    public ThreadPoolSizeConfig getThreadPoolSizeConfig() {
        if (rawConfig == null) {
            throw new SigletError("rawConfig is null");
        }
        return rawConfig.getGlobalConfigThreadPoolSize().chain(ThreadPoolSizeConfig.of(this));
    }
}
