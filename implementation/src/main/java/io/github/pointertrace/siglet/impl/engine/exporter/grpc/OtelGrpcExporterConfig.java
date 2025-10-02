package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.raw.ExporterConfig;
import io.github.pointertrace.siglet.impl.config.raw.ExporterConfigSetter;
import io.github.pointertrace.siglet.impl.config.raw.QueueSizeConfig;
import io.github.pointertrace.siglet.impl.config.raw.ThreadPoolSizeConfig;
import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.located.Location;

import java.net.InetSocketAddress;
import java.util.Objects;

public class OtelGrpcExporterConfig implements QueueSizeConfig, ExporterConfigSetter, Describable {

    private ExporterConfig exporterConfig;

    private InetSocketAddress address;

    private Location addressLocation;

    private Integer batchSizeInSignals;

    private Location batchSizeInSignalsLocation;

    private Integer batchTimeoutInMillis;

    private Location batchTimeoutInMillisLocation;

    private Integer queueSize;

    private Location queueSizeLocation;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public Integer getBatchSizeInSignals() {
        return Objects.requireNonNullElse(this.batchSizeInSignals, 1);
    }

    public void setBatchSizeInSignals(Integer batchSizeInSignals) {
        this.batchSizeInSignals = batchSizeInSignals;
    }

    public Integer getBatchTimeoutInMillis() {
        return Objects.requireNonNullElse(this.batchTimeoutInMillis, 0);
    }

    public void setBatchTimeoutInMillis(Integer batchTimeoutInMillis) {
        this.batchTimeoutInMillis = batchTimeoutInMillis;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(addressLocation.describe());
        sb.append("  address: ");
        sb.append(address);
        sb.append("\n");

        if (batchSizeInSignals != null) {
            sb.append(Describable.prefix(level));
            sb.append(batchSizeInSignalsLocation.describe());
            sb.append("  batch-size-in-signal: ");
            sb.append(batchSizeInSignals);
            sb.append("\n");
        }

        if (batchTimeoutInMillis != null) {
            sb.append(Describable.prefix(level));
            sb.append(batchTimeoutInMillisLocation.describe());
            sb.append("  batch-timeout-in-millis: ");
            sb.append(batchTimeoutInMillis);
            sb.append("\n");
        }

        if (queueSize != null) {
            sb.append(Describable.prefix(level));
            sb.append(queueSizeLocation.describe());
            sb.append("  queue-size: ");
            sb.append(queueSize);
            sb.append("\n");
        }
        return sb.toString();
    }

    public Location getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(Location addressLocation) {
        this.addressLocation = addressLocation;
    }

    public Location getBatchSizeInSignalsLocation() {
        return batchSizeInSignalsLocation;
    }

    public void setBatchSizeInSignalsLocation(Location batchSizeInSignalsLocation) {
        this.batchSizeInSignalsLocation = batchSizeInSignalsLocation;
    }

    public Location getBatchTimeoutInMillisLocation() {
        return batchTimeoutInMillisLocation;
    }

    public void setBatchTimeoutInMillisLocation(Location batchTimeoutInMillisLocation) {
        this.batchTimeoutInMillisLocation = batchTimeoutInMillisLocation;
    }

    @Override
    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Location getQueueSizeLocation() {
        return queueSizeLocation;
    }

    public void setQueueSizeLocation(Location queueSizeLocation) {
        this.queueSizeLocation = queueSizeLocation;
    }

    @Override
    public void setExporterConfig(ExporterConfig exporterConfig) {
        this.exporterConfig = exporterConfig;
    }


    public QueueSizeConfig getQueueSizeConfig() {
        if (exporterConfig.getRawConfig() == null) {
            throw new SigletError("rawConfig is null");
        }
        return exporterConfig.getRawConfig().getGlobalConfigQueueSize().chain(QueueSizeConfig.of(this));
    }

    public ThreadPoolSizeConfig getThreadPoolSizeConfig() {
        if (exporterConfig.getRawConfig() == null) {
            throw new SigletError("rawConfig is null");
        }
        return exporterConfig.getRawConfig().getGlobalConfigThreadPoolSize().chain(ThreadPoolSizeConfig.of(this));
    }
}
