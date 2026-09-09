package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.config.descriptor.InetSocketAddressTransform;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.component.config.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverType;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class OtelGrpcReceiverType implements ReceiverType<OtelGrpcReceiverConfig> {

    @Override
    public String getType() {
        return "grpc";
    }

    @Override
    public ConfigurationFactory<OtelGrpcReceiverConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(
                        property("address", OtelGrpcReceiverConfig::setAddress, string().transform(new InetSocketAddressTransform()).customErrorMessage("#location Address must be a valid IP:port format"))
                                .customErrorMessage("Invalid grpc receiver address at #location:", ""),
                        optionalProperty("queue-size", OtelGrpcReceiverConfig::setQueueSize,
                                integerValueObject().customErrorMessage("#location Queue size must be an integer"))
                                .customErrorMessage("Invalid queue-size property at #location:", ""),
                        optionalProperty("thread-pool-size", OtelGrpcReceiverConfig::setThreadPoolSize,
                                integerValueObject().customErrorMessage("#location Thread pool size must be an integer")).
                                customErrorMessage("Invalid thread-pool-size property at #location:", ""),
                        optionalProperty("max-inbound-message-size-bytes", OtelGrpcReceiverConfig::setMaxInboundMessageSizeBytes,
                                integerValueObject().customErrorMessage("#location max-inbound-message-size-bytes must be an integer"))
                                .customErrorMessage("Invalid max inbound message size at #location:", ""),
                        optionalProperty("max-inbound-metadata-size-bytes", OtelGrpcReceiverConfig::setMaxInboundMetadataSizeBytes,
                                integerValueObject().customErrorMessage("#location max-inbound-metadata-size-bytes must be an integer"))
                                .customErrorMessage("Invalid max inbound metadata size at #location:", ""),
                        optionalProperty("flow-control-window-bytes", OtelGrpcReceiverConfig::setFlowControlWindowBytes,
                                integerValueObject().customErrorMessage("#location flow-control-window-bytes must be an integer"))
                                .customErrorMessage("Invalid flow control window at #location:", ""),
                        optionalProperty("max-concurrent-calls-per-connection", OtelGrpcReceiverConfig::setMaxConcurrentCallsPerConnection,
                                integerValueObject().customErrorMessage("#location max-concurrent-calls-per-connection must be an integer"))
                                .customErrorMessage("Invalid max concurrent calls at #location:", ""),
                        optionalProperty("keep-alive-time-seconds", OtelGrpcReceiverConfig::setKeepAliveTimeSeconds,
                                integerValueObject().customErrorMessage("#location keep-alive-time-seconds must be an integer"))
                                .customErrorMessage("Invalid keep alive time at #location:", ""),
                        optionalProperty("keep-alive-timeout-seconds", OtelGrpcReceiverConfig::setKeepAliveTimeoutSeconds,
                                integerValueObject().customErrorMessage("#location keep-alive-timeout-seconds must be an integer"))
                                .customErrorMessage("Invalid keep alive timeout at #location:", ""),
                        optionalProperty("permit-keep-alive-time-seconds", OtelGrpcReceiverConfig::setPermitKeepAliveTimeSeconds,
                                integerValueObject().customErrorMessage("#location permit-keep-alive-time-seconds must be an integer"))
                                .customErrorMessage("Invalid permit keep alive time at #location:", "")
                ), OtelGrpcReceiverConfig.class
        );
    }

    @Override
    public ComponentCreator<ReceiverNode> getComponentCreator() {
        return (context, receiverNode) -> new OtelGrpcReceiver(context, receiverNode);
    }
}
