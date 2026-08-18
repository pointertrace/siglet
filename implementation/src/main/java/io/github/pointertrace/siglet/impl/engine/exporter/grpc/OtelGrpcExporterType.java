package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.impl.config.descriptor.InetSocketAddressTransform;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.component.config.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterType;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class OtelGrpcExporterType implements ExporterType<OtelGrpcExporterConfig> {


    @Override
    public String getType() {
        return "grpc";
    }

    @Override
    public ConfigurationFactory<OtelGrpcExporterConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(
                        property("address", OtelGrpcExporterConfig::setAddress,
                                string().transform(new InetSocketAddressTransform()).customErrorMessage("#location Address must be a valid IP:port format")).customErrorMessage("Invalid grpc exporter address at #location:",""),
                        optionalProperty("batch-size-in-signals", OtelGrpcExporterConfig::setBatchSizeInSignals,
                                integerValueObject().customErrorMessage("#location Batch size must be a integer")).customErrorMessage("Invalid batch size at #location:",""),
                        optionalProperty("batch-timeout-in-millis", OtelGrpcExporterConfig::setBatchTimeoutInMillis,
                                integerValueObject().customErrorMessage("#location Batch timeout must be a integer")).customErrorMessage("Invalid batch timeout at #location:",""),
                        optionalProperty("queue-size", OtelGrpcExporterConfig::setQueueSize, integerValueObject().customErrorMessage("#location Queue size must be a integer")).customErrorMessage("Invalid queue size at #location:","")),
                OtelGrpcExporterConfig.class
        );
    }

    @Override
    public ComponentCreator<ExporterNode> getComponentCreator() {
        return (context, exporterNode) -> new OtelGrpcExporter(context, exporterNode);
    }


}
