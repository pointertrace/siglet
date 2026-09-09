package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.impl.config.descriptor.QueueSizeDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ThreadPoolSizeDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.LocatedInetSocketAddress;
import io.github.pointertrace.siglet.parser.IntegerValue;

import java.math.BigInteger;
import java.util.Objects;

public class OtelGrpcExporterConfig implements QueueSizeDescriptor {

    private LocatedInetSocketAddress address;

    private IntegerValue batchSizeInSignals;

    private IntegerValue batchTimeoutInMillis;

    private IntegerValue queueSize;

    public LocatedInetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(LocatedInetSocketAddress address) {
        this.address = address;
    }

    public IntegerValue getBatchSizeInSignals() {
        return Objects.requireNonNullElseGet(batchSizeInSignals, () -> new IntegerValue(BigInteger.valueOf(1000)));
    }

    public void setBatchSizeInSignals(IntegerValue batchSizeInSignals) {
        this.batchSizeInSignals = batchSizeInSignals;
    }

    public IntegerValue getBatchTimeoutInMillis() {
        return Objects.requireNonNullElseGet(batchTimeoutInMillis, () -> new IntegerValue(BigInteger.valueOf(1000)));
    }

    public void setBatchTimeoutInMillis(IntegerValue batchTimeoutInMillis) {
        this.batchTimeoutInMillis = batchTimeoutInMillis;
    }

    public IntegerValue getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(IntegerValue queueSize) {
        this.queueSize = queueSize;
    }

}
