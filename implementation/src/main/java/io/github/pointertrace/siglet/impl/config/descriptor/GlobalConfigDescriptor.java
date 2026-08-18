package io.github.pointertrace.siglet.impl.config.descriptor;


import io.github.pointertrace.siglet.parser.*;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class GlobalConfigDescriptor implements Locatable {

    private Location location;

    private IntegerValue queueSize;

    private IntegerValue threadPoolSize;

    private LocatedUrl internalMetricsEndpointUrl;

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

    public LocatedUrl getInternalMetricsEndpointUrl() {
        return internalMetricsEndpointUrl;
    }

    protected void setInternalMetricsExporter(LocatedUrl internalMetricsEndpointUrl) {
        this.internalMetricsEndpointUrl = internalMetricsEndpointUrl;
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
                        .customErrorMessage("#location Queue size must be a integer"))
                        .customErrorMessage("Invalid queue size at #location"))
                .addOptionalProperty(property("thread-pool-size", GlobalConfigDescriptor::setThreadPoolSize, integerValueObject()
                        .customErrorMessage("#location Thread pool size must be a integer"))
                        .customErrorMessage("Invalid thread pool size at #location"))
                .addOptionalProperty(property("internal-metrics-endpoint-url", GlobalConfigDescriptor::setInternalMetricsExporter, string().transform(new LocatedUrlTransform())
                .customErrorMessage("#location Internal metrics endpoint url must be a string"))
                .customErrorMessage("Invalid internal metrics endpoint url at #location"))
                .addOptionalProperty(property("internal-metrics-export-interval-millis",
                        GlobalConfigDescriptor::setInternalMetricsExportIntervalMillis,
                        integerValueObject().customErrorMessage("#location Internal metrics export interval must be a integer"))
                        .customErrorMessage("Invalid internal metrics export interval at #location"));

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
