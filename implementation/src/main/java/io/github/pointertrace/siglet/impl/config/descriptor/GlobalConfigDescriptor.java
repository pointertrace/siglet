package io.github.pointertrace.siglet.impl.config.descriptor;


import io.github.pointertrace.siglet.parser.*;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class GlobalConfigDescriptor implements Locatable {

    private Location location;

    private IntegerValue queueSize;

    private IntegerValue threadPoolSize;

    private StringValue internalMetricsExporter;

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

    public StringValue getInternalMetricsExporter() {
        return internalMetricsExporter;
    }

    protected void setInternalMetricsExporter(StringValue internalMetricsExporter) {
        this.internalMetricsExporter = internalMetricsExporter;
    }

    public static Schema.Builder<?, GlobalConfigDescriptor> descriptorSchemaBuilder() {
        return object(GlobalConfigDescriptor::new)
                .addOptionalProperty(property("queue-size", GlobalConfigDescriptor::setQueueSize, integerValueObject()
                        .customErrorMessage("#location Queue size must be a integer"))
                        .customErrorMessage("Invalid queue size at #location"))
                .addOptionalProperty(property("thread-pool-size", GlobalConfigDescriptor::setThreadPoolSize, integerValueObject()
                        .customErrorMessage("#location Thread pool size must be a integer"))
                        .customErrorMessage("Invalid thread pool size at #location"))
                .addOptionalProperty(property("internal-metrics-exporter", GlobalConfigDescriptor::setInternalMetricsExporter, stringValueObject()
                .customErrorMessage("#location Internal metrics exporter must be a string"))
                .customErrorMessage("Invalid internal metrics exporter at #location"));

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
