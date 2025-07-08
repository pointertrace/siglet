package com.siglet.container.adapter;

import com.google.protobuf.Message;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import com.siglet.container.adapter.common.ProtoEventAdapter;
import com.siglet.container.adapter.common.ProtoInstrumentationScopeAdapter;
import com.siglet.container.adapter.common.ProtoResourceAdapter;
import com.siglet.container.adapter.metric.*;
import com.siglet.container.adapter.trace.ProtoLinkAdapter;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.adapter.trace.ProtoStatusAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.*;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;

import java.util.function.Function;
import java.util.function.Supplier;

public record AdapterConfig<M extends Message, B extends Message.Builder>(Supplier<Adapter<M, B>> adapterCreator, Function<M, B> messageToBuilder,
                                                                          Function<B, M> builderToMessage, Class<? extends Adapter<M, B>> adapter) {

    public static final AdapterConfig<KeyValue, KeyValue.Builder> KEY_VALUE_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoAttributesAdapter.KeyValueAdapter::new,
                    KeyValue::toBuilder,
                    KeyValue.Builder::build,
                    ProtoAttributesAdapter.KeyValueAdapter.class);

    public static final AdapterConfig<Span, Span.Builder> SPAN_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoSpanAdapter::new,
                    Span::toBuilder,
                    Span.Builder::build,
                    ProtoSpanAdapter.class);

    public static final AdapterConfig<Span.Event, Span.Event.Builder> EVENT_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoEventAdapter::new,
                    Span.Event::toBuilder,
                    Span.Event.Builder::build,
                    ProtoEventAdapter.class);

    public static final AdapterConfig<InstrumentationScope, InstrumentationScope.Builder> SCOPE_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoInstrumentationScopeAdapter::new,
                    InstrumentationScope::toBuilder,
                    InstrumentationScope.Builder::build,
                    ProtoInstrumentationScopeAdapter.class);

    public static final AdapterConfig<Resource, Resource.Builder> RESOURCE_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoResourceAdapter::new,
                    Resource::toBuilder,
                    Resource.Builder::build,
                    ProtoResourceAdapter.class);

    public static final AdapterConfig<Span.Link, Span.Link.Builder> LINK_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoLinkAdapter::new,
                    Span.Link::toBuilder,
                    Span.Link.Builder::build,
                    ProtoLinkAdapter.class);

    public static final AdapterConfig<Status, Status.Builder> STATUS_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoStatusAdapter::new,
                    Status::toBuilder,
                    Status.Builder::build,
                    ProtoStatusAdapter.class);

    public static final AdapterConfig<ExponentialHistogramDataPoint.Buckets, ExponentialHistogramDataPoint.Buckets.Builder>
            BUCKETS_ADAPTER_CONFIG = new AdapterConfig<>(
            ProtoBucketsAdapter::new,
            ExponentialHistogramDataPoint.Buckets::toBuilder,
            ExponentialHistogramDataPoint.Buckets.Builder::build,
            ProtoBucketsAdapter.class);

    public static final AdapterConfig<ExponentialHistogramDataPoint.Buckets, ExponentialHistogramDataPoint.Buckets.Builder>
            POSITIVE_BUCKETS_ADAPTER_CONFIG = new AdapterConfig<>(
            ProtoBucketsAdapter::new,
            ExponentialHistogramDataPoint.Buckets::toBuilder,
            ExponentialHistogramDataPoint.Buckets.Builder::build,
            ProtoBucketsAdapter.class);

    public static final AdapterConfig<ExponentialHistogramDataPoint.Buckets, ExponentialHistogramDataPoint.Buckets.Builder>
            NEGATIVE_BUCKETS_ADAPTER_CONFIG = new AdapterConfig<>(
            ProtoBucketsAdapter::new,
            ExponentialHistogramDataPoint.Buckets::toBuilder,
            ExponentialHistogramDataPoint.Buckets.Builder::build,
            ProtoBucketsAdapter.class);

    public static final AdapterConfig<Exemplar, Exemplar.Builder> EXEMPLAR_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoExemplarAdapter::new,
                    Exemplar::toBuilder,
                    Exemplar.Builder::build,
                    ProtoExemplarAdapter.class);

    public static final AdapterConfig<ExponentialHistogram, ExponentialHistogram.Builder>
            EXPONENTIAL_HISTOGRAM_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoExponentialHistogramAdapter::new,
                    ExponentialHistogram::toBuilder,
                    ExponentialHistogram.Builder::build,
                    ProtoExponentialHistogramAdapter.class);

    public static final AdapterConfig<ExponentialHistogramDataPoint, ExponentialHistogramDataPoint.Builder>
            EXPONENTIAL_HISTOGRAM_DATAPOINT_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoExponentialHistogramDataPointAdapter::new,
                    ExponentialHistogramDataPoint::toBuilder,
                    ExponentialHistogramDataPoint.Builder::build,
                    ProtoExponentialHistogramDataPointAdapter.class);

    public static final AdapterConfig<Gauge, Gauge.Builder> GAUGE_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoGaugeAdapter::new,
                    Gauge::toBuilder,
                    Gauge.Builder::build,
                    ProtoGaugeAdapter.class);

    public static final AdapterConfig<Histogram, Histogram.Builder> HISTOGRAM_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoHistogramAdapter::new,
                    Histogram::toBuilder,
                    Histogram.Builder::build,
                    ProtoHistogramAdapter.class);

    public static final AdapterConfig<HistogramDataPoint, HistogramDataPoint.Builder> HISTOGRAM_DATA_POINT_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoHistogramDataPointAdapter::new,
                    HistogramDataPoint::toBuilder,
                    HistogramDataPoint.Builder::build,
                    ProtoHistogramDataPointAdapter.class);

    public static final AdapterConfig<NumberDataPoint, NumberDataPoint.Builder> NUMBER_DATA_POINT_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoNumberDataPointAdapter::new,
                    NumberDataPoint::toBuilder,
                    NumberDataPoint.Builder::build,
                    ProtoNumberDataPointAdapter.class);

    public static final AdapterConfig<Sum, Sum.Builder> SUM_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoSumAdapter::new,
                    Sum::toBuilder,
                    Sum.Builder::build,
                    ProtoSumAdapter.class);

    public static final AdapterConfig<Summary, Summary.Builder> SUMMARY_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoSummaryAdapter::new,
                    Summary::toBuilder,
                    Summary.Builder::build,
                    ProtoSummaryAdapter.class);

    public static final AdapterConfig<SummaryDataPoint, SummaryDataPoint.Builder> SUMMARY_DATA_POINT_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoSummaryDataPointAdapter::new,
                    SummaryDataPoint::toBuilder,
                    SummaryDataPoint.Builder::build,
                    ProtoSummaryDataPointAdapter.class);

    public static final AdapterConfig<SummaryDataPoint.ValueAtQuantile, SummaryDataPoint.ValueAtQuantile.Builder>
            SUMMARY_DATA_POINT_VALUE_AT_QUANTILE_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoValueAtQuantileAdapter::new,
                    SummaryDataPoint.ValueAtQuantile::toBuilder,
                    SummaryDataPoint.ValueAtQuantile.Builder::build,
                    ProtoValueAtQuantileAdapter.class);

    public static final AdapterConfig<Metric, Metric.Builder> METRIC_ADAPTER_CONFIG =
            new AdapterConfig<>(
                    ProtoMetricAdapter::new,
                    Metric::toBuilder,
                    Metric.Builder::build,
                    ProtoMetricAdapter.class);

}




