package io.github.pointertrace.siglet.impl.config.graph;

import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ProcessorNode extends BaseNode {

    private List<BaseNode> to = new ArrayList<>();

    private PipelineNode pipeline;

    private io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor processor;

    public ProcessorNode(ProcessorDescriptor item) {
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
    public ProcessorDescriptor getDescription() {
        return (ProcessorDescriptor) super.getDescription();
    }

    public Map<String, String> getDestinationMappings() {
        return getDescription().getTo().stream()
                .filter(t -> t.getValue().contains(":"))
                .collect(Collectors.toMap(t -> t.getValue().split(":")[0], t -> t.getValue().split(":")[1]));
    }

}
