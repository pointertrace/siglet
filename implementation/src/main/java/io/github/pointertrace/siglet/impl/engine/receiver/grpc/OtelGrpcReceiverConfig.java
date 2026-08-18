package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.config.descriptor.QueueSizeDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ThreadPoolSizeDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.LocatedInetSocketAddress;
import io.github.pointertrace.siglet.parser.IntegerValue;

import java.math.BigInteger;
import java.util.Objects;

public class OtelGrpcReceiverConfig implements ThreadPoolSizeDescriptor, QueueSizeDescriptor {

    private LocatedInetSocketAddress address;

    private IntegerValue queueSize;

    private IntegerValue threadPoolSize;

    private IntegerValue maxInboundMessageSizeBytes;

    private IntegerValue maxInboundMetadataSizeBytes;

    private IntegerValue flowControlWindowBytes;

    private IntegerValue maxConcurrentCallsPerConnection;

    private IntegerValue keepAliveTimeSeconds;

    private IntegerValue keepAliveTimeoutSeconds;

    private IntegerValue permitKeepAliveTimeSeconds;

    public LocatedInetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(LocatedInetSocketAddress address) {
        this.address = address;
    }

    public IntegerValue getMaxInboundMessageSizeBytes() {
        return Objects.requireNonNullElseGet(maxInboundMessageSizeBytes,
                () -> new IntegerValue(BigInteger.valueOf(16 * 1024 * 1024)));
    }

    public void setMaxInboundMessageSizeBytes(IntegerValue maxInboundMessageSizeBytes) {
        this.maxInboundMessageSizeBytes = maxInboundMessageSizeBytes;
    }

    public IntegerValue getMaxInboundMetadataSizeBytes() {
        return Objects.requireNonNullElseGet(maxInboundMetadataSizeBytes,
                () -> new IntegerValue(BigInteger.valueOf(16 * 1024)));
    }

    public void setMaxInboundMetadataSizeBytes(IntegerValue maxInboundMetadataSizeBytes) {
        this.maxInboundMetadataSizeBytes = maxInboundMetadataSizeBytes;
    }

    public IntegerValue getFlowControlWindowBytes() {
        return Objects.requireNonNullElseGet(flowControlWindowBytes,
                () -> new IntegerValue(BigInteger.valueOf(1024 * 1024)));
    }

    public void setFlowControlWindowBytes(IntegerValue flowControlWindowBytes) {
        this.flowControlWindowBytes = flowControlWindowBytes;
    }

    public IntegerValue getMaxConcurrentCallsPerConnection() {
        return Objects.requireNonNullElseGet(maxConcurrentCallsPerConnection,
                () -> new IntegerValue(BigInteger.valueOf(1024)));
    }

    public void setMaxConcurrentCallsPerConnection(IntegerValue maxConcurrentCallsPerConnection) {
        this.maxConcurrentCallsPerConnection = maxConcurrentCallsPerConnection;
    }

    public IntegerValue getKeepAliveTimeSeconds() {
        return Objects.requireNonNullElseGet(keepAliveTimeSeconds,
                () -> new IntegerValue(BigInteger.valueOf(30)));
    }

    public void setKeepAliveTimeSeconds(IntegerValue keepAliveTimeSeconds) {
        this.keepAliveTimeSeconds = keepAliveTimeSeconds;
    }

    public IntegerValue getKeepAliveTimeoutSeconds() {
        return Objects.requireNonNullElseGet(keepAliveTimeoutSeconds,
                () -> new IntegerValue(BigInteger.valueOf(10)));
    }

    public void setKeepAliveTimeoutSeconds(IntegerValue keepAliveTimeoutSeconds) {
        this.keepAliveTimeoutSeconds = keepAliveTimeoutSeconds;
    }

    public IntegerValue getPermitKeepAliveTimeSeconds() {
        return Objects.requireNonNullElseGet(permitKeepAliveTimeSeconds,
                () -> new IntegerValue(BigInteger.valueOf(20)));
    }

    public void setPermitKeepAliveTimeSeconds(IntegerValue permitKeepAliveTimeSeconds) {
        this.permitKeepAliveTimeSeconds = permitKeepAliveTimeSeconds;
    }

    public IntegerValue getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(IntegerValue queueSize) {
        this.queueSize = queueSize;
    }

    public IntegerValue getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(IntegerValue threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
