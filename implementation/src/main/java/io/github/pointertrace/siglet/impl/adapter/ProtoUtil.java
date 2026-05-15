package io.github.pointertrace.siglet.impl.adapter;

import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import io.github.pointertrace.siglet.api.signal.trace.SpanKind;
import io.github.pointertrace.siglet.api.signal.trace.StatusCode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ProtoUtil {
    private ProtoUtil() {
    }

    public static byte[] toTraceId(long high, long low) {
        return ByteBuffer.allocate(16).putLong(high).putLong(low).array();
    }

    public static byte[] toSpanId(long spanId) {
        return ByteBuffer.allocate(8).putLong(spanId).array();
    }

    public static long readLong(byte[] value, int offset) {
        if (value == null || value.length < offset + 8) {
            return 0L;
        }
        return ByteBuffer.wrap(value, offset, 8).getLong();
    }

    public static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static io.opentelemetry.proto.trace.v1.Span.SpanKind toProto(SpanKind kind) {
        switch (kind) {
            case INTERNAL:
                return io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_INTERNAL;
            case SERVER:
                return io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_SERVER;
            case CLIENT:
                return io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_CLIENT;
            case PRODUCER:
                return io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_PRODUCER;
            case CONSUMER:
                return io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_CONSUMER;
            case UNRECOGNIZED:
                return io.opentelemetry.proto.trace.v1.Span.SpanKind.UNRECOGNIZED;
            case UNSPECIFIED:
            default:
                return io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_UNSPECIFIED;
        }
    }

    public static SpanKind fromProto(io.opentelemetry.proto.trace.v1.Span.SpanKind kind) {
        switch (kind) {
            case SPAN_KIND_INTERNAL:
                return SpanKind.INTERNAL;
            case SPAN_KIND_SERVER:
                return SpanKind.SERVER;
            case SPAN_KIND_CLIENT:
                return SpanKind.CLIENT;
            case SPAN_KIND_PRODUCER:
                return SpanKind.PRODUCER;
            case SPAN_KIND_CONSUMER:
                return SpanKind.CONSUMER;
            case UNRECOGNIZED:
                return SpanKind.UNRECOGNIZED;
            case SPAN_KIND_UNSPECIFIED:
            default:
                return SpanKind.UNSPECIFIED;
        }
    }

    public static io.opentelemetry.proto.trace.v1.Status.StatusCode toProto(StatusCode code) {
        switch (code) {
            case OK:
                return io.opentelemetry.proto.trace.v1.Status.StatusCode.STATUS_CODE_OK;
            case ERROR:
                return io.opentelemetry.proto.trace.v1.Status.StatusCode.STATUS_CODE_ERROR;
            case UNSET:
            default:
                return io.opentelemetry.proto.trace.v1.Status.StatusCode.STATUS_CODE_UNSET;
        }
    }

    public static StatusCode fromProto(io.opentelemetry.proto.trace.v1.Status.StatusCode code) {
        switch (code) {
            case STATUS_CODE_OK:
                return StatusCode.OK;
            case STATUS_CODE_ERROR:
                return StatusCode.ERROR;
            case STATUS_CODE_UNSET:
            case UNRECOGNIZED:
            default:
                return StatusCode.UNSET;
        }
    }

    public static io.opentelemetry.proto.metrics.v1.AggregationTemporality toProto(AggregationTemporality value) {
        switch (value) {
            case DELTA:
                return io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA;
            case CUMULATIVE:
            default:
                return io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE;
        }
    }

    public static AggregationTemporality fromProto(io.opentelemetry.proto.metrics.v1.AggregationTemporality value) {
        switch (value) {
            case AGGREGATION_TEMPORALITY_DELTA:
                return AggregationTemporality.DELTA;
            case AGGREGATION_TEMPORALITY_CUMULATIVE:
            case AGGREGATION_TEMPORALITY_UNSPECIFIED:
            case UNRECOGNIZED:
            default:
                return AggregationTemporality.CUMULATIVE;
        }
    }

    public static List<io.opentelemetry.proto.common.v1.KeyValue> fromMap(Map<String, Object> map) {
        List<io.opentelemetry.proto.common.v1.KeyValue> values = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            values.add(io.opentelemetry.proto.common.v1.KeyValue.newBuilder()
                    .setKey(entry.getKey())
                    .setValue(toAnyValue(entry.getValue()))
                    .build());
        }
        return values;
    }

    public static io.opentelemetry.proto.common.v1.AnyValue toAnyValue(Object value) {
        return AdapterUtils.objectToAnyValue(value);
    }
}


