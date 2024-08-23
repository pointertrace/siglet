package com.siglet.data.unmodifiable.metric;

public enum AggregationTemporality {

    DELTA,

    CUMULATIVE;

    public static AggregationTemporality valueOf(
            io.opentelemetry.proto.metrics.v1.AggregationTemporality protoAggregationTemporality) {
        return switch (protoAggregationTemporality) {
            case AGGREGATION_TEMPORALITY_UNSPECIFIED, UNRECOGNIZED -> null;
            case AGGREGATION_TEMPORALITY_DELTA -> DELTA;
            case AGGREGATION_TEMPORALITY_CUMULATIVE -> CUMULATIVE;
        };
    }

    public static io.opentelemetry.proto.metrics.v1.AggregationTemporality toProto(
            AggregationTemporality aggregationTemporality) {
        return switch (aggregationTemporality) {
            case null -> io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_UNSPECIFIED;
            case DELTA -> io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA;
            case CUMULATIVE -> io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE;
        };
    }


}
