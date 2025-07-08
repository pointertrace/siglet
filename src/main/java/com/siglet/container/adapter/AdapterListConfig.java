package com.siglet.container.adapter;

import com.google.protobuf.Message;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import com.siglet.container.adapter.common.ProtoEventAdapter;
import com.siglet.container.adapter.common.ProtoEventsAdapter;
import com.siglet.container.adapter.metric.*;
import com.siglet.container.adapter.trace.ProtoLinkAdapter;
import com.siglet.container.adapter.trace.ProtoLinksAdapter;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.*;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.function.Supplier;

public record AdapterListConfig<
        M extends Message,
        B extends Message.Builder,
        A extends Adapter<M, B>,
        L extends AdapterList<M, B, A>>(
        Supplier<AdapterList<M, B, A>> adapterListCreator,
        Supplier<A> adapterCreator,
        Supplier<B> builderCreator) {

    public static final AdapterListConfig<KeyValue, KeyValue.Builder, ProtoAttributesAdapter.KeyValueAdapter,
            ProtoAttributesAdapter>
            ATTRIBUTES_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoAttributesAdapter::new,
                    ProtoAttributesAdapter.KeyValueAdapter::new,
                    KeyValue::newBuilder);

    public static final AdapterListConfig<ExponentialHistogramDataPoint,
            ExponentialHistogramDataPoint.Builder, ProtoExponentialHistogramDataPointAdapter,
            ProtoExponentialHistogramDataPointsAdapter>
            EXPONENTIAL_HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoExponentialHistogramDataPointsAdapter::new,
                    ProtoExponentialHistogramDataPointAdapter::new,
                    ExponentialHistogramDataPoint::newBuilder);

    public static final AdapterListConfig<Exemplar, Exemplar.Builder, ProtoExemplarAdapter, ProtoExemplarsAdapter>
            EXEMPLARS_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoExemplarsAdapter::new,
                    ProtoExemplarAdapter::new,
                    Exemplar::newBuilder);

    public static final AdapterListConfig<NumberDataPoint, NumberDataPoint.Builder, ProtoNumberDataPointAdapter,
            ProtoNumberDataPointsAdapter>
            NUMBER_DATA_POINTS_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoNumberDataPointsAdapter::new,
                    ProtoNumberDataPointAdapter::new,
                    NumberDataPoint::newBuilder);

    public static final AdapterListConfig<HistogramDataPoint, HistogramDataPoint.Builder,
            ProtoHistogramDataPointAdapter, ProtoHistogramDataPointsAdapter>
            HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoHistogramDataPointsAdapter::new,
                    ProtoHistogramDataPointAdapter::new,
                    HistogramDataPoint::newBuilder);

    public static final AdapterListConfig<SummaryDataPoint, SummaryDataPoint.Builder,
            ProtoSummaryDataPointAdapter, ProtoSummaryDataPointsAdapter>
            SUMMARY_DATA_POINTS_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoSummaryDataPointsAdapter::new,
                    ProtoSummaryDataPointAdapter::new,
                    SummaryDataPoint::newBuilder);

    public static final AdapterListConfig<SummaryDataPoint.ValueAtQuantile, SummaryDataPoint.ValueAtQuantile.Builder,
            ProtoValueAtQuantileAdapter, ProtoValueAtQuantilesAdapter>
            VALUE_AT_QUANTILE_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoValueAtQuantilesAdapter::new,
                    ProtoValueAtQuantileAdapter::new,
                    SummaryDataPoint.ValueAtQuantile::newBuilder);

    public static final AdapterListConfig<Span.Link, Span.Link.Builder, ProtoLinkAdapter, ProtoLinksAdapter>
            LINKS_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoLinksAdapter::new,
                    ProtoLinkAdapter::new,
                    Span.Link::newBuilder);

    public static final AdapterListConfig<Span.Event, Span.Event.Builder, ProtoEventAdapter, ProtoEventsAdapter>
            EVENTS_ADAPTER_CONFIG =
            new AdapterListConfig<>(
                    ProtoEventsAdapter::new,
                    ProtoEventAdapter::new,
                    Span.Event::newBuilder);
}