package io.github.pointertrace.siglet.impl.engine;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

public class SigletMetrics {

    private final MeterRegistry meterRegistry;

    public SigletMetrics() {

        LoggingMetricExporter exporter = LoggingMetricExporter.create();

        PeriodicMetricReader reader =
                PeriodicMetricReader.builder(exporter)
                        .setInterval(Duration.ofSeconds(5))
                        .build();

        SdkMeterProvider meterProvider =
                SdkMeterProvider.builder()
                        .registerMetricReader(reader)
                        .build();

        OpenTelemetry openTelemetry =
                OpenTelemetrySdk.builder()
                        .setMeterProvider(meterProvider)
                        .build();

        meterRegistry = new SimpleMeterRegistry();

    }


    public Timer createTimer(String name, String description, Map<String, String> tags) {
        return Timer.builder(name)
                .description(description)
                .tags(tags.entrySet().stream()
                        .map(entry -> new ImmutableTag(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toUnmodifiableList()))
                .register(meterRegistry);
    }

    ;
}
