package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Processors {

    private static final Logger LOGGER = LoggerFactory.getLogger(Processors.class);

    private final Map<String, Processor> processorRegistry = new HashMap<>();

    public Processor create(SigletContext sigletContext, ProcessorNode processorNode) {
        return processorRegistry.put(processorNode.getName(), sigletContext.createProcessor(processorNode));
    }

    public Processor getProcessor(String name) {
        return processorRegistry.values().stream()
                .filter(processor -> processor.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void start() {
        processorRegistry.values().forEach(Processor::start);
    }

    public void stop() {
        processorRegistry.values().forEach(Processor::stop);

    }

    public void forEach(Consumer<Processor> processorConsumer) {
        processorRegistry.values().forEach(processorConsumer);
    }
}
