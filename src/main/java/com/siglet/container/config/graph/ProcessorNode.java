package com.siglet.container.config.graph;

import com.siglet.SigletError;
import com.siglet.container.config.raw.EventLoopConfig;
import com.siglet.container.config.raw.ProcessorConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ProcessorNode extends BaseNode implements EventLoopConfig {

    private List<BaseNode> to = new ArrayList<>();

    private PipelineNode pipeline;

    private Integer queueSize;

    private Integer threadPoolSize;

    ProcessorNode(ProcessorConfig item) {
        super(item);
    }

    public List<BaseNode> getTo() {
        return to;
    }

    public void setTo(List<BaseNode> to) {
        this.to = to;
    }

    public PipelineNode getPipeline() {
        return pipeline;
    }

    public void setPipeline(PipelineNode pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public ProcessorConfig getConfig() {
        return (ProcessorConfig) super.getConfig();
    }

    public Map<String, String> getDestinationMappings() {
        return getConfig().getTo().stream()
                .filter(t -> t.getValue().contains(":"))
                .collect(Collectors.toMap(t -> t.getValue().split(":")[0], t -> t.getValue().split(":")[1]));
    }

    @Override
    public Integer getQueueSize() {
        if (queueSize == null) {
            throw new SigletError(String.format("Queue size is not calculated for processor %s",getName()));
        }
        return queueSize;
    }

    @Override
    public Integer getThreadPoolSize() {
        if (threadPoolSize == null) {
            throw new SigletError(String.format("Thread pool size is not calculated for processor %s",getName()));
        }
        return threadPoolSize;
    }

    public void calculateEventLoopConfig(EventLoopConfig globalEventLoopConfig) {
        EventLoopConfig eventLoopConfig = globalEventLoopConfig.chain(getConfig());
        this.queueSize = eventLoopConfig.getQueueSize();
        this.threadPoolSize = eventLoopConfig.getThreadPoolSize();

    }
}
