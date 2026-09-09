package io.github.pointertrace.siglet.impl.engine.metric.otelgrpc;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.engine.metric.*;
import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;
import io.github.pointertrace.siglet.impl.engine.metric.LongGauge;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcRequest;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import io.opentelemetry.instrumentation.runtimetelemetry.RuntimeTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentSelector;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.View;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OtelGrpcMetrics implements Metrics {


    private final Meter meter;

    private final Map<String, ObservableLongCounter> counterMetrics = new ConcurrentHashMap<>();

    private final Map<String, ObservableLongGauge> gaugeMetrics = new ConcurrentHashMap<>();

    private final Map<String, LongHistogram> timerMetrics = new ConcurrentHashMap<>();

    private final Map<String, Measurements> metricMeasurements = new ConcurrentHashMap<>();

    private final InternalMetricsOtelExporter internalMetricsOtelExporter;

    private final OpenTelemetrySdk openTelemetrySdk;

    private final List<AutoCloseable> closeables = new ArrayList<>();

    private RuntimeTelemetry runtimeTelemetry;

    public OtelGrpcMetrics(long exportIntervalMillis, Exporter exporter) {

        if (exportIntervalMillis <= 0) {
            throw new SigletError("Internal metrics export interval must be greater than zero");
        }

        internalMetricsOtelExporter = new InternalMetricsOtelExporter(exporter.getSignalDestination().getSignalReceiverFunction());

        SdkMeterProvider meterProvider =
                SdkMeterProvider.builder()
                        .registerView(
                                InstrumentSelector.builder()
                                        .setName("rpc.*")
                                        .build(),
                                View.builder()
                                        .setAttributeFilter(
                                                attributes -> attributes.startsWith("rpc.") ||
                                                        attributes.startsWith("server") ||
                                                        attributes.equals("receiver") ||
                                                        attributes.equals("exporter") ||
                                                        attributes.equals("signal"))
                                        .build()
                        )
                        .registerMetricReader(PeriodicMetricReader.builder(internalMetricsOtelExporter)
                                .setInterval(exportIntervalMillis, TimeUnit.MILLISECONDS)
                                .build())
                        .build();

//        SdkMeterProvider meterProvider =
//                SdkMeterProvider.builder()
//                        .registerMetricReader(PeriodicMetricReader.builder(new LoggingMetricExporter())
//                               .setInterval(exportIntervalMillis, TimeUnit.MILLISECONDS)
//                                .build())
//                        .build();


        this.openTelemetrySdk = OpenTelemetrySdk.builder().setMeterProvider(meterProvider).build();

        this.meter = openTelemetrySdk.getMeter("siglet-internal-metrics");

    }

    @Override
    public OtelGrpcLongCounter createDroppedSignalsCounter(String componentName) {

        return createCounter(
                SIGNALS_DROPPED,
                "Signals dropped by component",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), componentName
                )
        );

    }


    @Override
    public OtelGrpcLongCounter createMissedSignalsCounter(String sourceName, String destinationName) {

        return createCounter(
                SIGNALS_MISSED,
                "Signals sent by source and missed by component because queue is full",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), destinationName,
                        AttributeKey.stringKey("source"), sourceName
                )
        );

    }

    @Override
    public LongCounter createAcceptedSignalsCounter(String sourceName, String destinationName) {


        return createCounter(
                SIGNALS_ACCEPTED,
                "Signals sent by source and accepted inserted into component queue",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), destinationName,
                        AttributeKey.stringKey("source"), sourceName
                )
        );

    }


    @Override
    public LongCounter createReceivedSignalsCounter(String componentName) {

        return createCounter(
                SIGNALS_RECEIVED,
                "Signals received by component",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), componentName
                )
        );


    }

    @Override
    public LongGauge createGrpcReceiverPackageSizeGauge(String componentName, String client, String signalType) {

        return createGauge(
                GRPC_PACKAGE_SIZE,
                "Package size in signals received by gRPC receiver",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("grpc-receiver"), componentName,
                        AttributeKey.stringKey("client"), client,
                        AttributeKey.stringKey("signal-type"), signalType
                )
        );

    }

    @Override
    public LongGauge createGrpcSenderPackageSizeGauge(String componentName, String signalType) {


        return createGauge(
                GRPC_PACKAGE_SIZE,
                "Package size in signals sent by gRPC sender",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("grpc-sender"), componentName,
                        AttributeKey.stringKey("signal-type"), signalType
                )
        );


    }

    @Override
    public LongCounter createQueueMissedSignalsCounter(String parentComponent, String eventLoopName) {

        return createCounter(
                EVENT_LOOP_SIGNALS_MISSED,
                "Signals missed by event loop because queue is full",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), parentComponent,
                        AttributeKey.stringKey("event-loop"), eventLoopName
                )
        );


    }

    @Override
    public LongCounter createQueueAcceptedSignalsCounter(String parentComponent, String eventLoopName) {

        return createCounter(
                EVENT_LOOP_SIGNALS_ACCEPTED,
                "Signals accepted into event loop queue",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), parentComponent,
                        AttributeKey.stringKey("event-loop"), eventLoopName
                )
        );


    }

    @Override
    public LongCounter createQueueReceivedSignalsCounter(String parentComponent, String eventLoopName) {


        return createCounter(
                EVENT_LOOP_SIGNALS_RECEIVED,
                "Signals received by event loop",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), parentComponent,
                        AttributeKey.stringKey("event-loop"), eventLoopName
                )
        );


    }


    @Override
    public LongCounter createEmittedSignalsCounter(String componentName) {


        return createCounter(
                SIGNALS_EMITTED,
                "Signals emitted by component",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), componentName
                )
        );


    }

    @Override
    public void createQueueMetrics(String parentComponent, String eventLoop, MeteredBlockingQueue<?> queue) {

        createCounter(
                EVENT_LOOP_QUEUE_SIZE,
                "Current number of signals in queue",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), parentComponent,
                        AttributeKey.stringKey("event-loop"), eventLoop
                ),
                () -> Long.valueOf(queue.size())
        );

        createCounter(
                EVENT_LOOP_QUEUE_CAPACITY,
                "Queue capacity",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), parentComponent,
                        AttributeKey.stringKey("event-loop"), eventLoop
                ),
                () -> Long.valueOf(queue.size() + queue.remainingCapacity())

        );

        createCounter(
                EVENT_LOOP_QUEUE_SIZE_MAX,
                "Max number of signals in queue since last observation",
                "{signal}",
                Attributes.of(
                        AttributeKey.stringKey("component"), parentComponent,
                        AttributeKey.stringKey("event-loop"), eventLoop
                ),
                () -> Long.valueOf(queue.getAndResetMaxSize())
        );

    }

    private record Measurement(Supplier<Long> valueSupplier, Attributes attributes) {
    }

    private static class Measurements {

        private final List<Measurement> measurements = new ArrayList<>();

        public void addMeasurement(LongAdder counter, Attributes attributes) {
            measurements.add(new Measurement(counter::longValue, attributes));
        }

        public void addMeasurement(AtomicLong gauge, Attributes attributes) {
            measurements.add(new Measurement(gauge::get, attributes));
        }

        public void addMeasurement(Supplier<Long> valueSupplier, Attributes attributes) {
            measurements.add(new Measurement(valueSupplier, attributes));
        }

        public Consumer<ObservableLongMeasurement> getMeasurementsCallBack() {
            return measurement -> {
                for (Measurement m : measurements) {
                    measurement.record(m.valueSupplier().get(), m.attributes());
                }
            };
        }
    }


    @Override
    public LongTimer createEventLoopProcessFunctionTimer(String parent, String eventLoop) {


        LongHistogram histogram = timerMetrics.computeIfAbsent(
                EVENT_LOOP_DURATION,
                key -> meter.histogramBuilder(EVENT_LOOP_DURATION)
                        .setDescription("Duration of event loop processing function")
                        .setUnit("ms")
                        .ofLongs()
                        .build()
        );

        return new OtelGrpcLongTimer(histogram,
                Attributes.of(
                        AttributeKey.stringKey("component"), parent,
                        AttributeKey.stringKey("event-loop"), eventLoop
                )
        );
    }

    @Override
    public LongTimer createQueueWaitTimer(String parent, String eventLoop) {

        LongHistogram histogram = timerMetrics.computeIfAbsent(
                EVENT_LOOP_QUEUE_WAIT,
                key -> meter.histogramBuilder(EVENT_LOOP_QUEUE_WAIT)
                        .setDescription("Time spent waiting in queue before processing")
                        .setUnit("ms")
                        .ofLongs()
                        .build()
        );

//                        .serviceLevelObjectives(
//                Duration.ofMillis(1),
//                Duration.ofMillis(5),
//                Duration.ofMillis(10),
//                Duration.ofMillis(20),
//                Duration.ofMillis(50),
//                Duration.ofMillis(100),
//                Duration.ofMillis(250),
//                Duration.ofMillis(500),
//                Duration.ofSeconds(1)
        return new OtelGrpcLongTimer(
                histogram,
                Attributes.of(
                        AttributeKey.stringKey("component"), parent,
                        AttributeKey.stringKey("event-loop"), eventLoop
                )
        );
    }

    @Override
    public ServerInterceptor createGrpcServerMetricsInterceptor(String componentName) {
        return GrpcTelemetry.builder(openTelemetrySdk)
                .addAttributesExtractor(
                        AttributesExtractor.constant(AttributeKey.stringKey("receiver"), componentName))
                .addServerAttributeExtractor(
                        new AttributesExtractor<GrpcRequest, Status>() {
                            @Override
                            public void onStart(AttributesBuilder attributes, Context parentContext, GrpcRequest grpcRequest) {
                                if (grpcRequest.getMethod().getFullMethodName().contains(".trace.")) {
                                    attributes.put(AttributeKey.stringKey("signal"), "trace");
                                } else if (grpcRequest.getMethod().getFullMethodName().contains(".metric.")) {
                                    attributes.put(AttributeKey.stringKey("signal"), "metric");
                                }
                            }

                            @Override
                            public void onEnd(AttributesBuilder attributes, Context context, GrpcRequest grpcRequest, @Nullable Status status, @Nullable Throwable error) {
                            }
                        }
                )
                .build()
                .createServerInterceptor();
    }

    @Override
    public ClientInterceptor createGrpcClientMetricsInterceptor(String componentName) {
        return GrpcTelemetry
                .builder(openTelemetrySdk)
                .addClientAttributeExtractor(
                        new AttributesExtractor<GrpcRequest, Status>() {
                            @Override
                            public void onStart(AttributesBuilder attributes, Context parentContext, GrpcRequest grpcRequest) {
                                if (grpcRequest.getMethod().getFullMethodName().contains(".trace.")) {
                                    attributes.put(AttributeKey.stringKey("signal"), "trace");
                                } else if (grpcRequest.getMethod().getFullMethodName().contains(".metric.")) {
                                    attributes.put(AttributeKey.stringKey("signal"), "metric");
                                }
                                attributes.put(AttributeKey.stringKey("exporter"), componentName);
                            }

                            @Override
                            public void onEnd(AttributesBuilder attributes, Context context, GrpcRequest grpcRequest, @Nullable Status status, @Nullable Throwable error) {
                            }
                        }
                )
                .build()
                .createClientInterceptor();
    }

    @Override
    public void start() {
        internalMetricsOtelExporter.start();
        runtimeTelemetry = RuntimeTelemetry.create(openTelemetrySdk);
    }

    @Override
    public void stop() {
        runtimeTelemetry.close();
        internalMetricsOtelExporter.close();
        for (AutoCloseable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception e) {
                // Handle the exception, e.g., log it
            }
        }
    }

    @Override
    public State getState() {
        return State.RUNNING;
    }

    @Override
    public String getName() {
        return "otel-grpc-metrics";
    }

    private OtelGrpcLongGauge createGauge(String name, String description, String unit, Attributes attributes) {

        AtomicLong gauge = new AtomicLong();

        Measurements measurements = this.metricMeasurements.computeIfAbsent(name, k -> new Measurements());

        measurements.addMeasurement(gauge, attributes);

        closeables.add(
                gaugeMetrics.computeIfAbsent(name, key -> meter
                        .gaugeBuilder(key)
                        .setDescription(description)
                        .setUnit(unit)
                        .ofLongs()
                        .buildWithCallback(measurements.getMeasurementsCallBack())
                )
        );

        return new OtelGrpcLongGauge(gauge);

    }

    private OtelGrpcLongCounter createCounter(String name, String description, String unit, Attributes attributes) {

        LongAdder adder = new LongAdder();

        Measurements measurements = this.metricMeasurements.computeIfAbsent(name, k -> new Measurements());

        measurements.addMeasurement(adder, attributes);

        closeables.add(
                counterMetrics.computeIfAbsent(
                        name,
                        key -> meter
                                .counterBuilder(key)
                                .setDescription(description)
                                .setUnit(unit)
                                .buildWithCallback(measurements.getMeasurementsCallBack())
                )
        );

        return new OtelGrpcLongCounter(adder);

    }

    private void createCounter(String name, String description, String unit, Attributes attributes, Supplier<Long> valueSupplier) {

        Measurements measurements = this.metricMeasurements.computeIfAbsent(name, k -> new Measurements());

        measurements.addMeasurement(valueSupplier, attributes);

        closeables.add(
                counterMetrics.computeIfAbsent(
                        name,
                        key -> meter
                                .counterBuilder(key)
                                .setDescription(description)
                                .setUnit(unit)
                                .buildWithCallback(measurements.getMeasurementsCallBack())
                )
        );

    }

    public static class InternalMetricsOtelExporter implements MetricExporter {

        private final SignalReceiverFunction signalReceiverFunction;

        private final AtomicBoolean started = new AtomicBoolean(false);

        public InternalMetricsOtelExporter(SignalReceiverFunction signalReceiverFunction) {
            this.signalReceiverFunction = signalReceiverFunction;
        }

        @Override
        public CompletableResultCode export(Collection<MetricData> metrics) {

            if (started.get()) {
                try {
                    boolean allReceived = true;
                    for (MetricData metricData : metrics) {
                        allReceived &= signalReceiverFunction.receive(metricData);
                    }
                    return allReceived ? CompletableResultCode.ofSuccess() : CompletableResultCode.ofFailure();
                } catch (Exception e) {
                    return CompletableResultCode.ofFailure();
                }
            } else {
                return CompletableResultCode.ofSuccess();
            }
        }

        public void start() {
            started.set(true);
        }

        @Override
        public CompletableResultCode flush() {
            return CompletableResultCode.ofSuccess();
        }

        @Override
        public CompletableResultCode shutdown() {
            return CompletableResultCode.ofSuccess();
        }

        @Override
        public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
            return AggregationTemporality.CUMULATIVE;
        }
    }
}
