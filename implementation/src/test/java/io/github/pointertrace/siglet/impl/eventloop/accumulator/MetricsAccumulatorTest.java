package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.impl.engine.pipeline.accumulator.MetricsAccumulator;
import io.github.pointertrace.siglet.impl.engine.pipeline.accumulator.SpansAccumulator;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


class MetricsAccumulatorTest {

    private Resource resource1;

    private Resource resource2;

    private InstrumentationScope instrumentationScope1;

    private InstrumentationScope instrumentationScope2;

    private Metric metric1;

    private Metric metric2;

    private Metric metric3;

    private Metric metric4;

    private MetricsAccumulator metricsAccumulator;

    @BeforeEach
    void setUp() {
        resource1 = Resource.newBuilder()
                .setDroppedAttributesCount(1)
                .build();

        resource2 = Resource.newBuilder()
                .setDroppedAttributesCount(2)
                .build();

        instrumentationScope1 = InstrumentationScope.newBuilder()
                .setName("instrumentationScope1")
                .build();

        instrumentationScope2 = InstrumentationScope.newBuilder()
                .setName("instrumentationScope2")
                .build();

        metric1 = Metric.newBuilder()
                .setName("metric1")
                .build();

        metric2 = Metric.newBuilder()
                .setName("metric2")
                .build();

        metric3 = Metric.newBuilder()
                .setName("metric3")
                .build();

        metric4 = Metric.newBuilder()
                .setName("metric4")
                .build();

        metricsAccumulator = new MetricsAccumulator();

    }

    @Test
    void add_metric_sameResource_sameInstrumentation() {

        metricsAccumulator.add(metric1, instrumentationScope1, resource1);
        metricsAccumulator.add(metric2, instrumentationScope1, resource1);
        metricsAccumulator.add(metric3, instrumentationScope1, resource1);
        metricsAccumulator.add(metric4, instrumentationScope1, resource1);

        ExportMetricsServiceRequest request = metricsAccumulator.getExportMetricsServiceRequest();
        checkPath(request, resource1, instrumentationScope1, metric1);
        checkPath(request, resource1, instrumentationScope1, metric2);
        checkPath(request, resource1, instrumentationScope1, metric3);
        checkPath(request, resource1, instrumentationScope1, metric4);

        checkResourceCount(request, 1);
        checkInstrumentationScopeCount(request, resource1, 1);
        checkMetricCount(request, resource1, instrumentationScope1, 4);

    }

    @Test
    void add_metric_sameResource_twoInstrumentation() {

        metricsAccumulator.add(metric1, instrumentationScope1, resource1);
        metricsAccumulator.add(metric2, instrumentationScope1, resource1);
        metricsAccumulator.add(metric3, instrumentationScope2, resource1);
        metricsAccumulator.add(metric4, instrumentationScope2, resource1);


        ExportMetricsServiceRequest request = metricsAccumulator.getExportMetricsServiceRequest();
        checkPath(request, resource1, instrumentationScope1, metric1);
        checkPath(request, resource1, instrumentationScope1, metric2);
        checkPath(request, resource1, instrumentationScope2, metric3);
        checkPath(request, resource1, instrumentationScope2, metric4);

        checkResourceCount(request, 1);

        checkInstrumentationScopeCount(request, resource1, 2);
        checkMetricCount(request, resource1, instrumentationScope1, 2);

        checkInstrumentationScopeCount(request, resource1, 2);
        checkMetricCount(request, resource1, instrumentationScope2, 2);
    }

    @Test
    void add_metric_twoResource_twoInstrumentation() {



        metricsAccumulator.add(metric1, instrumentationScope1, resource1);
        metricsAccumulator.add(metric2, instrumentationScope1, resource2);
        metricsAccumulator.add(metric3, instrumentationScope2, resource1);
        metricsAccumulator.add(metric4, instrumentationScope2, resource2);


        ExportMetricsServiceRequest request = metricsAccumulator.getExportMetricsServiceRequest();
        checkPath(request, resource1, instrumentationScope1, metric1);
        checkPath(request, resource2, instrumentationScope1, metric2);
        checkPath(request, resource1, instrumentationScope2, metric3);
        checkPath(request, resource2, instrumentationScope2, metric4);

        checkResourceCount(request, 2);

        checkInstrumentationScopeCount(request, resource1, 2);
        checkMetricCount(request, resource1, instrumentationScope1, 1);
        checkMetricCount(request, resource1, instrumentationScope2, 1);

        checkInstrumentationScopeCount(request, resource2, 2);
        checkMetricCount(request, resource2, instrumentationScope1, 1);
        checkMetricCount(request, resource2, instrumentationScope2, 1);


    }


    private void checkPath(ExportMetricsServiceRequest request, Resource resource, InstrumentationScope scope, Metric metric) {
        ResourceMetrics resourceMetrics = request.getResourceMetricsList().stream()
                .filter(rs -> rs.getResource().equals(resource))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("resource not found!"));

        ScopeMetrics scopeMetrics = resourceMetrics.getScopeMetricsList().stream()
                .filter(ss -> ss.getScope().equals(scope))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("scope not found!"));

        scopeMetrics.getMetricsList().stream()
                .filter(s -> s.equals(metric))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("metric not found!"));

    }

    private void checkResourceCount(ExportMetricsServiceRequest request, int resourceCount) {
        assertEquals(resourceCount, request.getResourceMetricsList().size());
    }

    private void checkInstrumentationScopeCount(ExportMetricsServiceRequest request, Resource resource, int scopeCount) {

        assertEquals(scopeCount, request.getResourceMetricsList().stream()
                .filter(rs -> rs.getResource() == resource)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("resource not found!"))
                .getScopeMetricsList().size());
    }

    private void checkMetricCount(ExportMetricsServiceRequest request, Resource resource, InstrumentationScope scope, int metricCount) {

        assertEquals(metricCount,  request.getResourceMetricsList().stream()
                .filter(rs -> rs.getResource() == resource)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("resource not found!"))
                .getScopeMetricsList().stream()
                .filter(ss -> ss.getScope() == scope)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("scope not found!"))
                .getMetricsList().size());
    }

}