package io.github.pointertrace.siglet.impl.engine.pipeline;

import io.github.pointertrace.siglet.impl.config.graph.PipelineNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.BaseGraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationProvider;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processors;

import java.util.List;

public class Pipeline extends BaseGraphComponent<PipelineNode> {

    private final Processors processors = new Processors();

    public Pipeline(SigletContext sigletContext, PipelineNode node) {
        super(sigletContext, node);
    }

    public Processors getProcessors() {
        return processors;
    }

    public List<Processor> getDeadEndProcessors() {
        return processors.getProcessors().stream()
                .filter(processor -> processor.getNode().getTo().isEmpty())
                .toList();
    }

    public SignalDestinationProvider getDestination(String name) {
        return processors.getProcessor(name);
    }

    @Override
    public void doStart() {
        processors.start();
    }

    @Override
    public void doStop() {
        processors.stop();
    }

    @Override
    public String getName() {
        return getNode().getName();
    }
}
