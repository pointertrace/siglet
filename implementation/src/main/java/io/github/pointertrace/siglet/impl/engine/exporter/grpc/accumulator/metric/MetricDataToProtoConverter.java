package io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.metric;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.*;
import io.opentelemetry.proto.metrics.v1.AggregationTemporality;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.metrics.data.*;

import java.util.ArrayList;
import java.util.List;

public class MetricDataToProtoConverter {


    private MetricDataToProtoConverter() {
    }

    public static Metric convertMetric(MetricData metric) {

        Metric.Builder builder = Metric.newBuilder()
                .setName(metric.getName());

        if (!metric.getDescription().isEmpty()) {
            builder.setDescription(metric.getDescription());
        }

        if (!metric.getUnit().isEmpty()) {
            builder.setUnit(metric.getUnit());
        }

        switch (metric.getType()) {

            case LONG_SUM:
                builder.setSum(convertLongSum(metric.getLongSumData()));
                break;

            case DOUBLE_SUM:
                builder.setSum(convertDoubleSum(metric.getDoubleSumData()));
                break;

            case LONG_GAUGE:
                builder.setGauge(convertLongGauge(metric.getLongGaugeData()));
                break;

            case DOUBLE_GAUGE:
                builder.setGauge(convertDoubleGauge(metric.getDoubleGaugeData()));
                break;

            case HISTOGRAM:
                builder.setHistogram(convertHistogram(metric.getHistogramData()));
                break;

            default:
                throw new IllegalArgumentException("Tipo não suportado: " + metric.getType());
        }

        return builder.build();
    }

    public static Resource convertResource(io.opentelemetry.sdk.resources.Resource resource) {
        Resource.Builder builder = Resource.newBuilder();
        builder.addAllAttributes(convertAttributes(resource.getAttributes()));
        return builder.build();
    }

    public static InstrumentationScope convertInstrumentationScope(InstrumentationScopeInfo scopeInfo) {
        InstrumentationScope.Builder builder =
                InstrumentationScope.newBuilder();
        if (scopeInfo.getName() != null) {
            builder.setName(scopeInfo.getName());
        }
        if (scopeInfo.getVersion() != null) {
            builder.setVersion(scopeInfo.getVersion());
        }
        scopeInfo.getAttributes().forEach((key, value) ->
                builder.addAttributes(
                        KeyValue.newBuilder()
                                .setKey(key.getKey())
                                .setValue(AnyValue.newBuilder()
                                        .setStringValue(String.valueOf(value)).build())
                                .build()));
        return builder.build();
    }

    protected static List<KeyValue> convertAttributes(Attributes attributes) {

        List<KeyValue> result = new ArrayList<>();

        attributes.forEach((key, value) -> {

            AnyValue anyValue =
                    AnyValue.newBuilder()
                            .setStringValue(String.valueOf(value))
                            .build();

            result.add(KeyValue.newBuilder()
                    .setKey(key.getKey())
                    .setValue(anyValue)
                    .build());
        });

        return result;
    }

    protected static Sum convertLongSum(SumData<LongPointData> data) {

        Sum.Builder builder = Sum.newBuilder()
                .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE)
                .setIsMonotonic(data.isMonotonic());

        for (LongPointData point : data.getPoints()) {
            builder.addDataPoints(convertNumberDataPoint(point));
        }

        return builder.build();
    }

    protected static Sum convertDoubleSum(SumData<DoublePointData> data) {

        Sum.Builder builder = Sum.newBuilder()
                .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE)
                .setIsMonotonic(data.isMonotonic());

        for (DoublePointData point : data.getPoints()) {
            builder.addDataPoints(convertNumberDataPoint(point));
        }

        return builder.build();
    }

    protected static Gauge convertLongGauge(GaugeData<LongPointData> data) {

        Gauge.Builder builder =
                Gauge.newBuilder();

        for (LongPointData point : data.getPoints()) {

            builder.addDataPoints(convertNumberDataPoint(point));
        }

        return builder.build();
    }

    protected static Gauge convertDoubleGauge(GaugeData<DoublePointData> data) {

        Gauge.Builder builder =
                Gauge.newBuilder();

        for (DoublePointData point : data.getPoints()) {

            builder.addDataPoints(convertNumberDataPoint(point));
        }

        return builder.build();
    }

    protected static NumberDataPoint convertNumberDataPoint(LongPointData point) {

        return NumberDataPoint.newBuilder()
                .setTimeUnixNano(point.getEpochNanos())
                .addAllAttributes(convertAttributes(point.getAttributes()))
                .setAsInt(point.getValue()).build();
    }

    protected static NumberDataPoint convertNumberDataPoint(DoublePointData point) {

        return NumberDataPoint.newBuilder()
                .setTimeUnixNano(point.getEpochNanos())
                .addAllAttributes(convertAttributes(point.getAttributes()))
                .setAsDouble(point.getValue()).build();
    }

    protected static Histogram convertHistogram(HistogramData data) {

        Histogram.Builder histogram =
                Histogram.newBuilder()
                        .setAggregationTemporality(
                                AggregationTemporality
                                        .AGGREGATION_TEMPORALITY_CUMULATIVE);

        for (HistogramPointData point : data.getPoints()) {

            HistogramDataPoint.Builder builder =
                    HistogramDataPoint.newBuilder()
                            .setCount(point.getCount())
                            .setTimeUnixNano(point.getEpochNanos())
                            .addAllAttributes(convertAttributes(point.getAttributes()))
                            .addAllExplicitBounds(point.getBoundaries())
                            .addAllBucketCounts(point.getCounts());

            builder.setSum(point.getSum());

            histogram.addDataPoints(builder.build());
        }

        return histogram.build();
    }
}