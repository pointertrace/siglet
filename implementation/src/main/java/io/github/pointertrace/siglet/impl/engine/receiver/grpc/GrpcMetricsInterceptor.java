package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.grpc.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcMetricsInterceptor implements ServerInterceptor {


    private final MeterRegistry registry;
    private final AtomicInteger activeCalls;

    public GrpcMetricsInterceptor(MeterRegistry registry) {
        this.registry = registry;

        this.activeCalls = registry.gauge(
                "siglet.grpc.server.active.calls",
                new AtomicInteger(0));
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String method =
                call.getMethodDescriptor().getFullMethodName();

        activeCalls.incrementAndGet();

        Counter.builder("siglet.grpc.server.requests")
                .tag("method", method)
                .register(registry)
                .increment();

        Timer.Sample sample = Timer.start(registry);

        ServerCall<ReqT, RespT> wrappedCall =
                new ForwardingServerCall.SimpleForwardingServerCall<>(call) {

                    @Override
                    public void close(Status status, Metadata trailers) {

                        Counter.builder("siglet.grpc.server.responses")
                                .tag("method", method)
                                .tag("status", status.getCode().name())
                                .register(registry)
                                .increment();

                        sample.stop(
                                Timer.builder("siglet.grpc.server.duration")
                                        .tag("method", method)
                                        .tag("status", status.getCode().name())
                                        .publishPercentileHistogram()
                                        .serviceLevelObjectives(
                                                Duration.ofMillis(1),
                                                Duration.ofMillis(2),
                                                Duration.ofMillis(5),
                                                Duration.ofMillis(10),
                                                Duration.ofMillis(20),
                                                Duration.ofMillis(50),
                                                Duration.ofMillis(100),
                                                Duration.ofMillis(250)
                                        )
                                        .register(registry));

                        activeCalls.decrementAndGet();

                        super.close(status, trailers);
                    }
                };

        ServerCall.Listener<ReqT> listener =
                next.startCall(wrappedCall, headers);

        return new ForwardingServerCallListener
                .SimpleForwardingServerCallListener<>(listener) {

            @Override
            public void onCancel() {

                Counter.builder("siglet.grpc.server.cancelled")
                        .tag("method", method)
                        .register(registry)
                        .increment();

                activeCalls.decrementAndGet();

                super.onCancel();
            }
        };
    }
}

