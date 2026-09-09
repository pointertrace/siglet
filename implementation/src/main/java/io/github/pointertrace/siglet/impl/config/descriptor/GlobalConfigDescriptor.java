package io.github.pointertrace.siglet.impl.config.descriptor;


import io.github.pointertrace.siglet.parser.*;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class GlobalConfigDescriptor implements Locatable {

    private Location location;

    private IntegerValue queueSize;

    private IntegerValue threadPoolSize;

    private StringValue internalMetricsGrpcExporter;

    private IntegerValue internalMetricsExportIntervalMillis;

    public IntegerValue getQueueSize() {
        return queueSize;
    }

    protected void setQueueSize(IntegerValue queueSize) {
        this.queueSize = queueSize;
    }

    public IntegerValue getThreadPoolSize() {
        return threadPoolSize;
    }

    protected void setThreadPoolSize(IntegerValue threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public StringValue getInternalMetricsGrpcExporter() {
        return internalMetricsGrpcExporter;
    }

    protected void setInternalMetricsGrpcExporter(StringValue internalMetricsGrpcExporter) {
        this.internalMetricsGrpcExporter = internalMetricsGrpcExporter;
    }

    public IntegerValue getInternalMetricsExportIntervalMillis() {
        return internalMetricsExportIntervalMillis;
    }

    protected void setInternalMetricsExportIntervalMillis(IntegerValue internalMetricsExportIntervalMillis) {
        this.internalMetricsExportIntervalMillis = internalMetricsExportIntervalMillis;
    }

    public static Schema.Builder<?, GlobalConfigDescriptor> descriptorSchemaBuilder() {
        return object(GlobalConfigDescriptor::new)
                .addOptionalProperty(property("queue-size", GlobalConfigDescriptor::setQueueSize, integerValueObject()
                        .customErrorMessage("#location Queue size must be an integer"))
                        .customErrorMessage("Invalid queue size at #location"))
                .addOptionalProperty(property("thread-pool-size", GlobalConfigDescriptor::setThreadPoolSize, integerValueObject()
                        .customErrorMessage("#location Thread pool size must be an integer"))
                        .customErrorMessage("Invalid thread pool size at #location"))
                .addOptionalProperty(property("internal-metrics-grpc-exporter", GlobalConfigDescriptor::setInternalMetricsGrpcExporter, stringValueObject()
                        .customErrorMessage("#location Internal metrics grpc exporter must be a string"))
                        .customErrorMessage("Invalid internal metrics grpc exporter at #location"))
                .addOptionalProperty(property("internal-metrics-export-interval-millis",
                        GlobalConfigDescriptor::setInternalMetricsExportIntervalMillis,
                        integerValueObject().customErrorMessage("#location Internal metrics export interval in millis must be an integer"))
                        .customErrorMessage("Invalid internal metrics export interval in millis at #location"));

    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
