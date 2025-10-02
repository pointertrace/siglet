package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.impl.config.raw.ExporterConfig;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterCreator;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ConfigDefinition;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.*;

public class OtelGrpcExporterType implements ExporterType {

    @Override
    public String getName() {
        return "grpc";
    }

    @Override
    public ConfigDefinition getConfigDefinition() {
        return () -> requiredProperty(ExporterConfig::setConfig, ExporterConfig::setConfigLocation, "config",
                strictObject(OtelGrpcExporterConfig::new,
                        requiredProperty(OtelGrpcExporterConfig::setAddress, OtelGrpcExporterConfig::setAddressLocation,
                                "address", text(inetSocketAddress())),
                        optionalProperty(OtelGrpcExporterConfig::setBatchSizeInSignals,
                                OtelGrpcExporterConfig::setBatchSizeInSignalsLocation, "batch-size-in-signals",
                                numberInt()),
                        optionalProperty(OtelGrpcExporterConfig::setBatchTimeoutInMillis,
                                OtelGrpcExporterConfig::setBatchTimeoutInMillisLocation,
                                "batch-timeout-in-millis", numberInt()),
                        optionalProperty(OtelGrpcExporterConfig::setQueueSize, OtelGrpcExporterConfig::setQueueSizeLocation,
                                "queue-size", numberInt())));
    }


    @Override
    public ExporterCreator getExporterCreator() {
        return (context, exporterNode) -> new OtelGrpcExporter(context, exporterNode);
    }
}
