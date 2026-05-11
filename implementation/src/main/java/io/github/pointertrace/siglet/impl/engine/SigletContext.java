package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.graph.*;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.impl.engine.receiver.Receiver;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverType;

public class SigletContext {

    private final Config config;

    private Graph graph;

    private final GraphFactory graphFactory = new GraphFactory();

    public SigletContext(Config config) {
        this.config = config;
    }

    public Graph getGraph() {
        if (graph == null) {
            graph = graphFactory.create(config.getYamlDescriptor());
        }
        return graph;
    }

    public Processor createProcessor(ProcessorNode processorNode) {
        ProcessorType<?> processorType = config.getProcessorTypeRegistry()
                .get(processorNode.getDescription().getType().getValue());
        // todo remover esse cast
        return (Processor) processorType.getComponentCreator().create(this, processorNode);
    }


    public Receiver createReceiver(ReceiverNode receiverNode) {
        ReceiverType<?> receiverType = config.getReceiverTypeRegistry()
                .get(receiverNode.getDescription().getType().getValue());
        // todo remover esse cast
        return (Receiver) receiverType.getComponentCreator().create(this, receiverNode);
    }

    public Exporter createExporter(ExporterNode exporterNode) {
        ExporterType<?> exporterType = config.getExporterTypeRegistry()
                .get(exporterNode.getDescription().getType().getValue());
        // todo remover esse cast
        return (Exporter) exporterType.getComponentCreator().create(this, exporterNode);
    }

    public Config getConfig() {
        return config;
    }

}
