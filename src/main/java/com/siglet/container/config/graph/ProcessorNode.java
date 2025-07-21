package com.siglet.container.config.graph;

import com.siglet.SigletError;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.config.raw.SignalType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ProcessorNode extends BaseNode {

    private List<BaseNode> to = new ArrayList<>();

    private PipelineNode pipeline;

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

    public SignalType getSignal() {
        return getConfig().getProcessorKind().getSignal();
    }
}
