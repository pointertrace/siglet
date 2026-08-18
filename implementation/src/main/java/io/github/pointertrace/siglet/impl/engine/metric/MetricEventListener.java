package io.github.pointertrace.siglet.impl.engine.metric;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.event.componentlifecycle.ComponentLifeCycleEventListener;
import io.github.pointertrace.siglet.impl.engine.event.connection.ConnectionEventListener;
import io.github.pointertrace.siglet.impl.engine.event.eventloop.EventLoopEventListener;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MetricEventListener implements ComponentLifeCycleEventListener, EventLoopEventListener,
        ConnectionEventListener {

    public static final String SIGNALS_RECEIVED = "siglet.signals.received";

    public static final String SIGNALS_ACCEPTED = "siglet.signals.accepted";

    public static final String SIGNALS_MISSED = "siglet.signals.missed";

    public static final String SIGNALS_DROPPED = "siglet.signals.dropped";

    public static final String SIGNALS_EMITTED = "siglet.signals.emitted";

    public static final String EXPORTER_EXPORTED_ACCEPTED = "siglet.exporter.signals.exporter";

    public static final String EXPORTER_EXPORTED_MISSED = "siglet.exporter.signals.missed";

    public static final String EVENT_LOOP_DURATION = "siglet.eventloop.duration";

    public static final String EVENT_LOOP_QUEUE_WAIT = "siglet.eventloop.queue.wait";

    public static final String EVENT_LOOP_QUEUE_SIZE = "siglet.eventloop.queue.size";

    public static final String EVENT_LOOP_QUEUE_CAPACITY = "siglet.eventloop.queue.capacity";

    public static final String EVENT_LOOP_QUEUE_SIZE_MAX = "siglet.eventloop.queue.size.max";

    private final MeterRegistry meterRegistry;

    private final Map<String, Timer> eventLoopDurationTimers = new HashMap<>();

    private final Map<String, Counter> signalsReceivedCounters = new HashMap<>();

    private final Map<String, Counter> signalsMissedCounters = new HashMap<>();

    private final Map<String, Counter> signalsDroppedCounters = new HashMap<>();

    private final Map<String, Counter> exportedAcceptedSignalsCounters = new HashMap<>();

    private final Map<String, Counter> exportedMissedSignalsCounters = new HashMap<>();

    private final Map<String, Counter> signalsAcceptedCounters = new HashMap<>();

    public MetricEventListener(long exportIntervalMillis, URL endpointUrl) {
        Objects.requireNonNull(endpointUrl, "endpointUrl");
        if (exportIntervalMillis <= 0) {
            throw new SigletError("Internal metrics export interval must be greater than zero");
        }

        meterRegistry = new OtlpMeterRegistry(new MeterConfig(endpointUrl, Duration.ofMillis(exportIntervalMillis)),
                Clock.SYSTEM, daemonThreadFactory());
        meterRegistry.config().meterFilter(new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                if (id.getName().startsWith("jvm.")) {
                    return id.withName("siglet." + id.getName());
                }
                return id;
            }
        });

        new ClassLoaderMetrics().bindTo(meterRegistry);
        new JvmMemoryMetrics().bindTo(meterRegistry);
        new JvmGcMetrics().bindTo(meterRegistry);
        new JvmThreadMetrics().bindTo(meterRegistry);
        new ProcessorMetrics().bindTo(meterRegistry);

    }

    static java.util.concurrent.ThreadFactory daemonThreadFactory() {
        return runnable -> {
            Thread thread = new Thread(runnable, "siglet-internal-metrics-exporter");
            thread.setDaemon(true);
            return thread;
        };
    }

    @Override
    public void afterInstantiation(long timestamp, Component component) {

    }

    @Override
    public void beforeStart(long timestamp, Component component) {

//        if (component instanceof Receiver receiver) {
//            createComponentMetrics(receiver.getName());
//            for (SignalDestination signalDestination : receiver.getSignalDestinations()) {
//                createSignalDestinationMetrics(receiver.getName(), signalDestination.getName());
//            }
//        } else if (component instanceof Processor processor) {
//            createComponentMetrics(processor.getName());
//            for (SignalDestination signalDestination : processor.getSignalDestinations()) {
//                createSignalDestinationMetrics(processor.getName(), signalDestination.getName());
//            }
//        } else if (component instanceof Exporter exporter) {
//            createComponentMetrics(exporter.getName());
//            createExporterMetrics(exporter.getName());
//        } else if (component instanceof EventLoop<?> eventLoop) {
//            createComponentMetrics(eventLoop.getName());
//            createEventLoopMetrics(eventLoop.getParentComponent().getName(), eventLoop.getName());
//            if (eventLoop instanceof ProcessorEventLoop<?> processorEventLoop) {
//                for (SignalDestination signalDestination : processorEventLoop.getSignalDestinations()) {
//                    createSignalDestinationMetrics(eventLoop.getName(), signalDestination.getName());
//                }
//            }
//        }

    }

    @Override
    public void afterStart(long timestamp, Component component) {

    }

    @Override
    public void beforeEnd(long timestamp, Component component) {

    }

    @Override
    public void afterEnd(long timestamp, Component component) {

    }

    private void createSignalDestinationMetrics(String componentName, String destinationName) {

        String key = componentName + ":" + destinationName;

        Counter accepted = createAcceptedSignalsCounter(componentName, destinationName);
        signalsAcceptedCounters.put(key, accepted);

        Counter missed = createMissedSignalsCounter(componentName, destinationName);
        signalsMissedCounters.put(key, missed);

        Counter dropped = createDroppedSignalsCounter(componentName, destinationName);
        signalsDroppedCounters.put(key, dropped);
    }

    private @NonNull Counter createDroppedSignalsCounter(String componentName, String destinationName) {
        Counter dropped = Counter.builder(SIGNALS_DROPPED)
                .tag("component", componentName)
                .tag("destination", destinationName)
                .register(meterRegistry);
        dropped.increment(0);
        return dropped;
    }

    private @NonNull Counter createMissedSignalsCounter(String componentName, String destinationName) {
        Counter missed = Counter.builder(SIGNALS_MISSED)
                .tag("component", componentName)
                .tag("destination", destinationName)
                .register(meterRegistry);
        missed.increment(0);
        return missed;
    }

    private @NonNull Counter createAcceptedSignalsCounter(String componentName, String destinationName) {
        Counter accepted = Counter.builder(SIGNALS_ACCEPTED).tag("component", componentName).tag("destination", destinationName)
                .register(meterRegistry);
        accepted.increment(0);
        return accepted;
    }

    private @NonNull Counter createReceivedSignalsCounter(String componentName) {
        Counter received = Counter.builder(SIGNALS_RECEIVED)
                .tag("component", componentName)
                .register(meterRegistry);

        received.increment(0);
        return received;
    }

    private @NonNull Counter createEmmitedSignalsCounter(String componentName) {
        Counter emitted = Counter.builder(SIGNALS_EMITTED)
                .tag("component", componentName)
                .register(meterRegistry);
        emitted.increment(0);
        return emitted;
    }

    private void createExporterMetrics(String exporterName) {

        Counter exportedAccepted = Counter.builder(EXPORTER_EXPORTED_ACCEPTED)
                .tag("exporter", exporterName)
                .register(meterRegistry);
        exportedAccepted.increment(0);
        exportedAcceptedSignalsCounters.put(exporterName, exportedAccepted);

        Counter exportedMissed = Counter.builder(EXPORTER_EXPORTED_MISSED)
                .tag("exporter", exporterName)
                .register(meterRegistry);
        exportedMissed.increment(0);
        exportedMissedSignalsCounters.put(exporterName, exportedMissed);
    }

    private void createComponentMetrics(String componentName) {

        Counter received = createReceivedSignalsCounter(componentName);
        signalsReceivedCounters.put(componentName, received);

    }


    @Override
    public <T> BlockingQueue<T> eventLoopQueueCreation(long timestamp, BaseEventLoop<?,?> eventLoop,
                                                       BlockingQueue<T> eventLoopQueue) {

        MeteredBlockingQueue<T> meteredBlockingQueue = new MeteredBlockingQueue<>(
                eventLoopQueue,
                createQueueWaitTimer(eventLoop.getParentComponent().getName(), eventLoop.getName()),
                createReceivedSignalsCounter(eventLoop.getParentComponent().getName()),
                createAcceptedSignalsCounter(eventLoop.getParentComponent().getName(), eventLoop.getName()),
                createMissedSignalsCounter(eventLoop.getParentComponent().getName(), eventLoop.getName()),
                createDroppedSignalsCounter(eventLoop.getParentComponent().getName(), eventLoop.getName())
        );

        createQueueMetrics(eventLoop.getParentComponent().getName(), eventLoop.getName(), meteredBlockingQueue);
        return meteredBlockingQueue;
    }

    @Override
    public <T> EmitterFunction<T> eventLoopEmitFunctionCreation(long timestamp, BaseEventLoop<?,?> eventLoop,
                                                                EmitterFunction<T> signalEmitterFunction) {

        Counter emitted = createEmmitedSignalsCounter(eventLoop.getName());
        return (T signal) -> {
            emitted.increment();
            signalEmitterFunction.emit(signal);
        };
    }

    @Override
    public <IN, OUT> Function<IN, OUT> eventLoopProcessFunctionCreation(
            long timestamp, BaseEventLoop<?,?> eventLoop, Function<IN, OUT> processFunction) {

        Timer timer = createEventLoopProcessFunctionTimer(eventLoop.getParentComponent().getName(), eventLoop.getName());
        return (IN in) -> {
            long start = System.nanoTime();
            try {
                return processFunction.apply(in);
            } finally {
                timer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            }
        };
    }

    @Override
    public SignalDestination beforeConnect(SignalSource signalSource, SignalDestination signalDestination) {
        return signalDestination;
//        return new SignalDestinationWrapper(
//                signalDestination,
//                createAcceptedSignalsCounter(signalSource.getName(), signalDestination.getName()),
//                createMissedSignalsCounter(signalSource.getName(), signalDestination.getName()),
//                createReceivedSignalsCounter(signalSource.getName())
//        );
    }

    private static class MeterConfig implements OtlpConfig {

        private final URL url;

        private final Duration step;

        private MeterConfig(URL url, Duration step) {
            this.url = url;
            this.step = step;
        }

        @Override
        public String url() {
            return url.toExternalForm();
        }

        @Override
        public Duration step() {
            return step;
        }


        @Override
        public @Nullable String get(String key) {
            return null;
        }
    }


    public void createQueueMetrics(String parentComponent, String eventLoop, MeteredBlockingQueue<?> queue) {

        Gauge.builder(EVENT_LOOP_QUEUE_SIZE,
                        queue,
                        BlockingQueue::size)
                .tag("component", parentComponent)
                .tags("event-loop", eventLoop)
                .description("Current queue size")
                .register(meterRegistry);

        Gauge.builder(EVENT_LOOP_QUEUE_CAPACITY,
                        queue,
                        q -> q.size() + q.remainingCapacity())
                .tag("component", parentComponent)
                .tags("event-loop", eventLoop)
                .description("Queue capacity")
                .register(meterRegistry);

        Gauge.builder(EVENT_LOOP_QUEUE_SIZE_MAX,
                        queue,
                        MeteredBlockingQueue::getAndResetMaxSize)
                .tag("component", parentComponent)
                .tags("event-loop", eventLoop)
                .description("Maximum queue size observed since last scrape")
                .register(meterRegistry);
    }

    public Timer createEventLoopProcessFunctionTimer(String parent, String eventLoop) {

        return Timer.builder(EVENT_LOOP_DURATION)
                .publishPercentiles(0.5, 0.95, 0.99)
                .distributionStatisticExpiry(Duration.ofSeconds(1))
                .distributionStatisticBufferLength(1)
                .tag("component", parent)
                .tags("event-loop", eventLoop)
                .register(meterRegistry);

    }

    public Timer createQueueWaitTimer(String parent, String eventLoop) {
        return Timer.builder(EVENT_LOOP_QUEUE_WAIT)
                .publishPercentileHistogram()
                .serviceLevelObjectives(
                        Duration.ofMillis(1),
                        Duration.ofMillis(5),
                        Duration.ofMillis(10),
                        Duration.ofMillis(20),
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(250),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1)
                )
                .tag("component", parent)
                .tags("event-loop", eventLoop)
                .register(meterRegistry);
    }

}
