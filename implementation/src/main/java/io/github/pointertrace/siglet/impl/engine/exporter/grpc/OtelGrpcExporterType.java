package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.config.descriptor.InetSocketAddressTransform;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterType;
import io.github.pointertrace.siglet.parser.impl.schema.SchemaPropertyBuilder;

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
                                string().transform(new InetSocketAddressTransform())),
                        optionalProperty("batch-size-in-signals", OtelGrpcExporterConfig::setBatchSizeInSignals,
                                integerValueObject()),
                        optionalProperty("batch-timeout-in-millis", OtelGrpcExporterConfig::setBatchTimeoutInMillis,
                                integerValueObject()),
                        optionalProperty("queue-size", OtelGrpcExporterConfig::setQueueSize, integerValueObject())),
                OtelGrpcExporterConfig.class
        );
    }

    @Override
    public ComponentCreator<ExporterNode> getComponentCreator() {
        return (context, exporterNode) -> new OtelGrpcExporter(context, exporterNode);
    }


}
