package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.graph.*;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterType;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptors;
import io.github.pointertrace.siglet.impl.engine.metric.MetricInterceptor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultFactoryImpl;
import io.github.pointertrace.siglet.impl.engine.receiver.Receiver;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverType;

public class SigletContextImpl implements SigletContext {

    private final Config config;

    private Graph graph;

    private final GraphFactory graphFactory = new GraphFactory();

    private final Interceptors interceptors = new Interceptors();

    public SigletContextImpl(Config config) {
        this.config = config;
        // todo porque aqui????
        ResultFactoryImpl.init();
    }

    @Override
    public Graph getGraph() {
        if (graph == null) {
            graph = graphFactory.create(config.getYamlDescriptor());
        }
        return graph;
    }

    @Override
    public Processor createProcessor(ProcessorNode processorNode) {
        ProcessorType<?> processorType = config.getProcessorTypeRegistry()
                .get(processorNode.getDescription().getType().getValue());
        // todo remover esse cast
        return (Processor) processorType.getComponentCreator().create(this, processorNode);
    }


    @Override
    public Receiver createReceiver(ReceiverNode receiverNode) {
        ReceiverType<?> receiverType = config.getReceiverTypeRegistry()
                .get(receiverNode.getDescription().getType().getValue());
        // todo remover esse cast
        return (Receiver) receiverType.getComponentCreator().create(this, receiverNode);
    }

    @Override
    public Exporter createExporter(ExporterNode exporterNode) {
        ExporterType<?> exporterType = config.getExporterTypeRegistry()
                .get(exporterNode.getDescription().getType().getValue());
        // todo remover esse cast
        return (Exporter) exporterType.getComponentCreator().create(this, exporterNode);
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public Interceptor getInterceptor() {
        return interceptors;
    }

    @Override
    public void addInterceptor(MetricInterceptor metricInterceptor) {
        interceptors.addInterceptor(metricInterceptor);
    }

}
