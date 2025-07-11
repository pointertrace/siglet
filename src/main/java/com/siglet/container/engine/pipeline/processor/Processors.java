package com.siglet.container.engine.pipeline.processor;

import com.siglet.SigletError;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.config.raw.ProcessorKind;
import com.siglet.container.engine.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Processors {

    private final Map<String, Processor> namedProcessors = new HashMap<>();

    public Processor create(Context context, ProcessorNode processorNode) {
        ProcessorConfig sigletConfig = processorNode.getConfig();
        if (sigletConfig.getKind() == ProcessorKind.SPANLET) {
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
