package io.github.pointertrace.siglet.impl.adapter;

import com.google.protobuf.ByteString;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import io.github.pointertrace.siglet.api.signal.trace.SpanKind;
import io.github.pointertrace.siglet.api.signal.trace.StatusCode;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.ArrayValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.common.v1.KeyValueList;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;

import java.nio.ByteBuffer;
import java.util.*;

import static io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality.CUMULATIVE;
import static io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality.DELTA;


public class AdapterUtils {

    private AdapterUtils() {
    }

    public static ByteString traceId(long high, long low) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(high);
        buffer.putLong(low);
        return ByteString.copyFrom(buffer.array());
    }

    public static ByteString spanId(long spanId) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(spanId);
        return ByteString.copyFrom(buffer.array());
    }

    public static long spanId(byte[] spanId) {
        return ByteBuffer.wrap(spanId).getLong();
    }

    public static String traceIdEx(ByteString traceId) {
        return Long.toHexString(traceIdHigh(traceId.toByteArray())) +
                Long.toHexString(traceIdLow(traceId.toByteArray()));
    }

    public static String spanIdEx(ByteString spanId) {
        return Long.toHexString(traceIdHigh(spanId.toByteArray()));
    }

    public static long traceIdHigh(byte[] traceId) {
        return ByteBuffer.wrap(Arrays.copyOfRange(traceId, 0, 8)).getLong();
    }

    public static long traceIdLow(byte[] traceId) {
        return ByteBuffer.wrap(Arrays.copyOfRange(traceId, 8, 16)).getLong();
    }

    public static Object anyValueToObject(AnyValue anyValue) {
        return switch (anyValue.getValueCase()) {
            case STRING_VALUE -> anyValue.getStringValue();
            case BOOL_VALUE -> anyValue.getBoolValue();
            case INT_VALUE -> anyValue.getIntValue();
            case DOUBLE_VALUE -> anyValue.getDoubleValue();
            case ARRAY_VALUE -> {
                ArrayValue v = anyValue.getArrayValue();
                Object[] result = new Object[v.getValuesCount()];
                for (int i = 0; i < v.getValuesCount(); i++) {
                    result[i] = anyValueToObject(v.getValues(i));
                }
                yield result;
            }
            case KVLIST_VALUE -> {
                KeyValueList kvList = anyValue.getKvlistValue();
                List<Map.Entry<String, Object>> result = new ArrayList<>(kvList.getValuesCount());
                kvList.getValuesList().forEach(kv ->
                        result.add(new AbstractMap.SimpleImmutableEntry<>(kv.getKey(), anyValueToObject(kv.getValue())))
                );
                yield result;
            }
            case BYTES_VALUE -> anyValue.getBytesValue().toByteArray();
            case VALUE_NOT_SET -> null;
        };
    }

    public static List<KeyValue> mapToKeyValueList(Map<String, Object> map) {

        List<KeyValue> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.add(KeyValue.newBuilder()
                    .setKey(entry.getKey())
                    .setValue(objectToAnyValue(entry.getValue()))
                    .build());
        }

        return result;
    }

    public static Map<String, Object> keyValueListToMap(List<KeyValue> keyValueList) {
        Map<String, Object> result = new HashMap<>();

        for (KeyValue kv : keyValueList) {
            result.put(kv.getKey(), anyValueToObject(kv.getValue()));
        }

        return result;
    }


    public static AnyValue objectToAnyValue(Object value) {
        return switch (value) {
            case String s -> AnyValue.newBuilder().setStringValue(s).build();
            case Boolean b -> AnyValue.newBuilder().setBoolValue(b).build();
            case Long l -> AnyValue.newBuilder().setIntValue(l).build();
            case Double d -> AnyValue.newBuilder().setDoubleValue(d).build();
            case Object[] a -> {
                ArrayValue.Builder avBuilder = ArrayValue.newBuilder();
                for (Object o : a) {
                    avBuilder.addValues(objectToAnyValue(o));
                }
                yield AnyValue.newBuilder().setArrayValue(avBuilder.build()).build();
            }
            case List<?> l -> {
                KeyValueList.Builder kvLstBld = KeyValueList.newBuilder();
                for (Object o : l) {
                    if (o instanceof Map.Entry<?, ?> e) {
                        kvLstBld.addValues(KeyValue.newBuilder()
                                .setKey(e.getKey().toString())
                                .setValue(objectToAnyValue(e.getValue()))
                                .build());
                    } else {
                        throw new SigletError("list item must be a Map.Entry!");
                    }
                }
                yield AnyValue.newBuilder().setKvlistValue(kvLstBld.build()).build();
            }
            case byte[] b -> AnyValue.newBuilder().setBytesValue(ByteString.copyFrom(b)).build();
            case null -> AnyValue.newBuilder().build();
            default -> throw new SigletError(value.getClass().getSimpleName() + "is not a valid attriute type!");
        };
    }

    public static SpanKind getKind(Span.SpanKind kind) {
        return switch (kind) {
            case SPAN_KIND_UNSPECIFIED, UNRECOGNIZED -> null;
            case SPAN_KIND_INTERNAL -> SpanKind.INTERNAL;
            case SPAN_KIND_SERVER -> SpanKind.SERVER;
            case SPAN_KIND_CLIENT -> SpanKind.CLIENT;
            case SPAN_KIND_PRODUCER -> SpanKind.PRODUCER;
            case SPAN_KIND_CONSUMER -> SpanKind.CONSUMER;
        };
    }

    public static Span.SpanKind getKind(SpanKind kind) {
        return switch (kind) {
            case INTERNAL -> Span.SpanKind.SPAN_KIND_INTERNAL;
            case SERVER -> Span.SpanKind.SPAN_KIND_SERVER;
            case CLIENT -> Span.SpanKind.SPAN_KIND_CLIENT;
            case PRODUCER -> Span.SpanKind.SPAN_KIND_PRODUCER;
            case CONSUMER -> Span.SpanKind.SPAN_KIND_CONSUMER;
            case OTHER -> null;
        };
    }

    public static StatusCode getStatusCode(Status.StatusCode statusCode) {
        return switch (statusCode) {
            case STATUS_CODE_OK -> StatusCode.OK;
            case STATUS_CODE_ERROR -> StatusCode.ERROR;
            default -> StatusCode.UNSET;
        };
    }

    public static Status.StatusCode getStatusCode(StatusCode statusCode) {
        return switch (statusCode) {
            case OK -> Status.StatusCode.STATUS_CODE_OK;
            case ERROR -> Status.StatusCode.STATUS_CODE_ERROR;
            default -> Status.StatusCode.STATUS_CODE_UNSET;
        };
    }

    public static io.opentelemetry.proto.metrics.v1.AggregationTemporality valueOf(
            AggregationTemporality aggregationTemporality) {
        return switch (aggregationTemporality) {
            case null -> io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_UNSPECIFIED;
            case DELTA -> io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA;
            case CUMULATIVE ->
                    io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE;
        };
    }

    public static AggregationTemporality valueOf(io.opentelemetry.proto.metrics.v1.AggregationTemporality aggregationTemporality) {
        return switch (aggregationTemporality) {
            case io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_UNSPECIFIED,
                 UNRECOGNIZED -> null;
            case io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA -> DELTA;
            case io.opentelemetry.proto.metrics.v1.AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE ->
                    CUMULATIVE;
        };
    }
}