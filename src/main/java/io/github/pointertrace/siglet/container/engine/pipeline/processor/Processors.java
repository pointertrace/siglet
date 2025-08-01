package io.github.pointertrace.siglet.container.engine.pipeline.processor;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.container.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.container.config.raw.ProcessorKind;
import io.github.pointertrace.siglet.container.engine.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Processors {

    private final Map<String, Processor> namedProcessors = new HashMap<>();

    public Processor create(Context context, ProcessorNode processorNode) {
        ProcessorConfig sigletConfig = processorNode.getConfig();
        if (sigletConfig.getProcessorKind() == ProcessorKind.SPANLET ||
                sigletConfig.getProcessorKind() == ProcessorKind.METRICLET) {
            return namedProcessors.put(processorNode.getName(), context.createProcessor(processorNode));
        } else {
            throw new SigletError("Cannot create a processor for a non-spanlet item");
        }
    }

    public Processor getDestination(String name) {
        return namedProcessors.values().stream()
                .filter(processor -> processor.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void start() {
        namedProcessors.values().forEach(Processor::start);
    }

    public void stop() {
        namedProcessors.values().forEach(Processor::stop);

    }

    public void forEach(Consumer<Processor> processorConsumer) {
        namedProcessors.values().forEach(processorConsumer);
    }
}
