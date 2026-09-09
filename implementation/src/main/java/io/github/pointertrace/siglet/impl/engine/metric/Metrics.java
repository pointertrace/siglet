package io.github.pointertrace.siglet.impl.engine.metric;

import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;

public interface Metrics extends Component {

    String SIGNALS_RECEIVED = "siglet.signals.received";

    String SIGNALS_ACCEPTED = "siglet.signals.accepted";

    String SIGNALS_MISSED = "siglet.signals.missed";

    String SIGNALS_DROPPED = "siglet.signals.dropped";

    String SIGNALS_EMITTED = "siglet.signals.emitted";

    String GRPC_PACKAGE_SIZE = "siglet.grpc.package.size";

    String EVENT_LOOP_SIGNALS_MISSED = "siglet.eventloop.signals.missed";

    String EVENT_LOOP_SIGNALS_ACCEPTED = "siglet.eventloop.signals.accepted";

    String EVENT_LOOP_SIGNALS_RECEIVED = "siglet.eventloop.signals.received";

    String EVENT_LOOP_DURATION = "siglet.eventloop.duration";

    String EVENT_LOOP_QUEUE_WAIT = "siglet.eventloop.queue.wait";

    String EVENT_LOOP_QUEUE_SIZE = "siglet.eventloop.queue.size";

    String EVENT_LOOP_QUEUE_CAPACITY = "siglet.eventloop.queue.capacity";

    String EVENT_LOOP_QUEUE_SIZE_MAX = "siglet.eventloop.queue.size.max";

    LongCounter createDroppedSignalsCounter(String componentName);

    LongCounter createMissedSignalsCounter(String sourceName, String destinationName);

    LongCounter createAcceptedSignalsCounter(String sourceName, String destinationName);

    LongCounter createReceivedSignalsCounter(String componentName);

    LongGauge createGrpcReceiverPackageSizeGauge(String componentName, String client, String signalType);

    LongGauge createGrpcSenderPackageSizeGauge(String componentName, String signalType);

    LongCounter  createQueueMissedSignalsCounter(String parentComponent, String eventLoopName);

    LongCounter createQueueAcceptedSignalsCounter(String parentComponent, String eventLoopName);

    LongCounter createQueueReceivedSignalsCounter(String parentComponent, String eventLoopName);

    LongCounter createEmittedSignalsCounter(String componentName);

    void createQueueMetrics(String parentComponent, String eventLoop, MeteredBlockingQueue<?> queue);

    LongTimer createEventLoopProcessFunctionTimer(String parent, String eventLoop);

    LongTimer createQueueWaitTimer(String parent, String eventLoop);

    ServerInterceptor createGrpcServerMetricsInterceptor(String componentName);

    ClientInterceptor createGrpcClientMetricsInterceptor(String componentName);
}
