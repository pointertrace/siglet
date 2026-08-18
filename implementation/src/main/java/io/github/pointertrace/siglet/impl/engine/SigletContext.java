package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.config.graph.Graph;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.engine.metric.MetricInterceptor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.engine.receiver.Receiver;

public interface SigletContext {

    Graph getGraph();

    Processor createProcessor(ProcessorNode processorNode);

    Receiver createReceiver(ReceiverNode receiverNode);

    Exporter createExporter(ExporterNode exporterNode);

    Config getConfig();

    Interceptor getInterceptor();

    void addInterceptor(MetricInterceptor metricInterceptor);
}
