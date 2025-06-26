package com.siglet.container.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.*;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoMetricAdapterAdapterTest {

    private Metric protoGaugeMetric;

    private Metric protoSumMetric;

    private Metric protoHistogramMetric;

    private Metric protoExponentialHistogramMetric;

    private Metric protoSummaryMetric;

    private Resource protoResource;

    private InstrumentationScope protoInstrumentationScope;

    private ProtoMetricAdapter protoGaugeMetricAdapter;

    private ProtoMetricAdapter protoSumMetricAdapter;

    private ProtoMetricAdapter protoHistogramMetricAdapter;

    private ProtoMetricAdapter protoExponentialHistogramMetricAdapter;

    private ProtoMetricAdapter protoSummaryMetricAdapter;

    @BeforeEach
    void setUp() {

        protoGaugeMetric = Metric.newBuilder()
                .setName("gauge-metric-name")
                .setDescription("gauge-metric-description")
                .setUnit("gauge-metric-unit")
                .setGauge(Gauge.newBuilder()
                        .addDataPoints(NumberDataPoint.newBuilder()
                                .setTimeUnixNano(1)
                                .setStartTimeUnixNano(2)
                                .setAsInt(10)
                                .build())
                        .build())
                .build();

        protoSumMetric = Metric.newBuilder()
                .setName("sum-metric-name")
                .setDescription("sum-metric-description")
                .setUnit("sum-metric-unit")
                .setSum(Sum.newBuilder()
                        .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE)
                        .setIsMonotonic(true)
                        .addDataPoints(NumberDataPoint.newBuilder()
                                .setTimeUnixNano(10)
                                .setStartTimeUnixNano(20)
                                .setAsDouble(10.10)
                                .build())
                        .build())
                .build();

        protoHistogramMetric = Metric.newBuilder()
                .setName("histogram-metric-name")
                .setDescription("histogram-metric-description")
                .setUnit("histogram-metric-unit")
                .setHistogram(Histogram.newBuilder()
                        .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA)
                        .addDataPoints(HistogramDataPoint.newBuilder()
                                .setTimeUnixNano(100)
                                .setStartTimeUnixNano(200)
                                .setSum(100.111)
                                .build())
                        .build())
                .build();

        protoExponentialHistogramMetric = Metric.newBuilder()
                .setName("exponential-histogram-metric-name")
                .setDescription("exponential-histogram-metric-description")
                .setUnit("exponential-histogram-metric-unit")
                .setExponentialHistogram(ExponentialHistogram.newBuilder()
                        .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA)
                        .addDataPoints(ExponentialHistogramDataPoint.newBuilder()
                                .setTimeUnixNano(1000)
                                .setStartTimeUnixNano(2000)
                                .setSum(1000.2222)
                                .build())
                        .build())
                .build();

        protoSummaryMetric = Metric.newBuilder()
                .setName("summary-metric-name")
                .setDescription("summary-metric-description")
                .setUnit("summary-metric-unit")
                .setSummary(Summary.newBuilder()
                        .addDataPoints(SummaryDataPoint.newBuilder()
                                .setTimeUnixNano(1000)
                                .setStartTimeUnixNano(2000)
                                .setSum(1000.2222)
                                .build())
                        .build())
                .build();

        protoResource = Resource.newBuilder()
                .setDroppedAttributesCount(2)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("rs-str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("rs-str-attribute-value").build())
                        .build())
                .build();

        protoInstrumentationScope = InstrumentationScope.newBuilder()
                .setName("instrumentation-scope-name")
                .setVersion("instrumentation-scope-version")
                .setDroppedAttributesCount(3)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("is-str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("is-str-attribute-value").build())
                        .build())
                .build();

        protoGaugeMetricAdapter = new ProtoMetricAdapter(protoGaugeMetric, protoResource, protoInstrumentationScope);
        protoSumMetricAdapter = new ProtoMetricAdapter(protoSumMetric, protoResource, protoInstrumentationScope);
        protoHistogramMetricAdapter = new ProtoMetricAdapter(protoHistogramMetric, protoResource, protoInstrumentationScope);
        protoExponentialHistogramMetricAdapter = new ProtoMetricAdapter(protoExponentialHistogramMetric, protoResource, protoInstrumentationScope);
        protoSummaryMetricAdapter = new ProtoMetricAdapter(protoSummaryMetric, protoResource, protoInstrumentationScope);

    }

    @Test
    void get_gaugeMetric() {

        assertEquals("gauge-metric-name", protoGaugeMetricAdapter.getName());
        assertEquals("gauge-metric-description", protoGaugeMetricAdapter.getDescription());
        assertEquals("gauge-metric-unit", protoGaugeMetricAdapter.getUnit());
        assertTrue(protoGaugeMetricAdapter.hasGauge());

        assertNotNull(protoGaugeMetricAdapter.getGauge());
        assertNotNull(protoGaugeMetricAdapter.getGauge().getDataPoints());
        assertEquals(1, protoGaugeMetricAdapter.getGauge().getDataPoints().getSize());
        ProtoNumberDataPointAdapter protoNumberDataPointAdapter = protoGaugeMetricAdapter.getGauge()
                .getDataPoints().getAt(0);

        assertEquals(1, protoNumberDataPointAdapter.getTimeUnixNano());
        assertEquals(2, protoNumberDataPointAdapter.getStartTimeUnixNano());
        assertEquals(10, protoNumberDataPointAdapter.getAsLong());

    }

    @Test
    void get_sumMetric() {

        assertEquals("sum-metric-name", protoSumMetricAdapter.getName());
        assertEquals("sum-metric-description", protoSumMetricAdapter.getDescription());
        assertEquals("sum-metric-unit", protoSumMetricAdapter.getUnit());
        assertTrue(protoSumMetricAdapter.hasSum());

        assertNotNull(protoSumMetricAdapter.getSum());
        assertNotNull(protoSumMetricAdapter.getSum().getDataPoints());
        assertEquals(1, protoSumMetricAdapter.getSum().getDataPoints().getSize());
        ProtoNumberDataPointAdapter protoNumberDataPointAdapter = protoSumMetricAdapter.getSum()
                .getDataPoints().getAt(0);

        assertEquals(10, protoNumberDataPointAdapter.getTimeUnixNano());
        assertEquals(20, protoNumberDataPointAdapter.getStartTimeUnixNano());
        assertEquals(10.10, protoNumberDataPointAdapter.getAsDouble());

    }

    @Test
    void get_histogramMetric() {

        assertEquals("histogram-metric-name", protoHistogramMetricAdapter.getName());
        assertEquals("histogram-metric-description", protoHistogramMetricAdapter.getDescription());
        assertEquals("histogram-metric-unit", protoHistogramMetricAdapter.getUnit());
        assertTrue(protoHistogramMetricAdapter.hasHistogram());

        assertNotNull(protoHistogramMetricAdapter.getHistogram());
        assertNotNull(protoHistogramMetricAdapter.getHistogram().getDataPoints());
        assertEquals(1, protoHistogramMetricAdapter.getHistogram().getDataPoints().getSize());
        ProtoHistogramDataPointAdapter protoHistogramDataPointAdapter = protoHistogramMetricAdapter.getHistogram()
                .getDataPoints().getAt(0);

        assertEquals(100, protoHistogramDataPointAdapter.getTimeUnixNano());
        assertEquals(200, protoHistogramDataPointAdapter.getStartTimeUnixNano());
        assertEquals(100.111, protoHistogramDataPointAdapter.getSum());

    }

    @Test
    void get_exponentialHistogramMetric() {

        assertEquals("exponential-histogram-metric-name", protoExponentialHistogramMetricAdapter.getName());
        assertEquals("exponential-histogram-metric-description",
                protoExponentialHistogramMetricAdapter.getDescription());
        assertEquals("exponential-histogram-metric-unit", protoExponentialHistogramMetricAdapter.getUnit());
        assertTrue(protoExponentialHistogramMetricAdapter.hasExponentialHistogram());

        assertNotNull(protoExponentialHistogramMetricAdapter.getExponentialHistogram());
        assertNotNull(protoExponentialHistogramMetricAdapter.getExponentialHistogram().getDataPoints());
        assertEquals(1, protoExponentialHistogramMetricAdapter.getExponentialHistogram().getDataPoints().getSize());
        ProtoExponentialHistogramDataPointAdapter protoExponentialHistogramDataPointAdapter =
                protoExponentialHistogramMetricAdapter.getExponentialHistogram()
                        .getDataPoints().getAt(0);

        assertEquals(1000, protoExponentialHistogramDataPointAdapter.getTimeUnixNano());
        assertEquals(2000, protoExponentialHistogramDataPointAdapter.getStartTimeUnixNano());
        assertEquals(1000.2222, protoExponentialHistogramDataPointAdapter.getSum());

    }

    @Test
    void get_summaryMetric() {

        assertEquals("summary-metric-name", protoSummaryMetricAdapter.getName());
        assertEquals("summary-metric-description", protoSummaryMetricAdapter.getDescription());
        assertEquals("summary-metric-unit", protoSummaryMetricAdapter.getUnit());
        assertTrue(protoSummaryMetricAdapter.hasSummary());

        assertNotNull(protoSummaryMetricAdapter.getSummary());
        assertNotNull(protoSummaryMetricAdapter.getSummary().getDataPoints());
        assertEquals(1, protoSummaryMetricAdapter.getSummary().getDataPoints().getSize());
        ProtoSummaryDataPointAdapter protoSummaryDataPointAdapter =
                protoSummaryMetricAdapter.getSummary().getDataPoints().getAt(0);

        assertEquals(1000, protoSummaryDataPointAdapter.getTimeUnixNano());
        assertEquals(2000, protoSummaryDataPointAdapter.getStartTimeUnixNano());
        assertEquals(1000.2222, protoSummaryDataPointAdapter.getSum());

    }


    @Test
    void setAndGet_gaugeMetric() {
        protoGaugeMetricAdapter
                .setName("new-name")
                .setDescription("new-description")
                .setUnit("new-unit")
                .getGauge().getDataPoints().getAt(0)
                .setTimeUnixNano(50)
                .setStartTimeUnixNano(60)
                .setAsLong(70)
                .setFlags(80);


        assertEquals("new-name", protoGaugeMetricAdapter.getName());
        assertEquals("new-description", protoGaugeMetricAdapter.getDescription());
        assertEquals("new-unit", protoGaugeMetricAdapter.getUnit());
        assertTrue(protoGaugeMetricAdapter.hasGauge());

        assertNotNull(protoGaugeMetricAdapter.getGauge());
        assertNotNull(protoGaugeMetricAdapter.getGauge().getDataPoints());
        assertEquals(1, protoGaugeMetricAdapter.getGauge().getDataPoints().getSize());
        ProtoNumberDataPointAdapter protoNumberDataPointAdapter = protoGaugeMetricAdapter.getGauge()
                .getDataPoints().getAt(0);

        assertEquals(50, protoNumberDataPointAdapter.getTimeUnixNano());
        assertEquals(60, protoNumberDataPointAdapter.getStartTimeUnixNano());
        assertEquals(70, protoNumberDataPointAdapter.getAsLong());
        assertEquals(80, protoNumberDataPointAdapter.getFlags());

    }

    @Test
    void setAndGet_sumMetric() {
        protoSumMetricAdapter
                .setName("new-name")
                .setDescription("new-description")
                .setUnit("new-unit")
                .getSum().getDataPoints().getAt(0)
                .setTimeUnixNano(50)
                .setStartTimeUnixNano(60)
                .setAsLong(70)
                .setFlags(80);


        assertEquals("new-name", protoSumMetricAdapter.getName());
        assertEquals("new-description", protoSumMetricAdapter.getDescription());
        assertEquals("new-unit", protoSumMetricAdapter.getUnit());
        assertTrue(protoSumMetricAdapter.hasSum());

        assertNotNull(protoSumMetricAdapter.getSum());
        assertNotNull(protoSumMetricAdapter.getSum().getDataPoints());
        assertEquals(1, protoSumMetricAdapter.getSum().getDataPoints().getSize());
        ProtoNumberDataPointAdapter protoNumberDataPointAdapter = protoSumMetricAdapter.getSum()
                .getDataPoints().getAt(0);

        assertEquals(50, protoNumberDataPointAdapter.getTimeUnixNano());
        assertEquals(60, protoNumberDataPointAdapter.getStartTimeUnixNano());
        assertEquals(70, protoNumberDataPointAdapter.getAsLong());
        assertEquals(80, protoNumberDataPointAdapter.getFlags());

    }

    @Test
    void setAndGet_exponentialHistogramMetric() {
        protoExponentialHistogramMetricAdapter
                .setName("new-name")
                .setDescription("new-description")
                .setUnit("new-unit")
                .getExponentialHistogram().getDataPoints().getAt(0)
                .setTimeUnixNano(50)
                .setStartTimeUnixNano(60)
                .setSum(70.70)
                .setFlags(80);


        assertEquals("new-name", protoExponentialHistogramMetricAdapter.getName());
        assertEquals("new-description", protoExponentialHistogramMetricAdapter.getDescription());
        assertEquals("new-unit", protoExponentialHistogramMetricAdapter.getUnit());
        assertTrue(protoExponentialHistogramMetricAdapter.hasExponentialHistogram());

        assertNotNull(protoExponentialHistogramMetricAdapter.getExponentialHistogram());
        assertNotNull(protoExponentialHistogramMetricAdapter.getExponentialHistogram().getDataPoints());
        assertEquals(1, protoExponentialHistogramMetricAdapter.getExponentialHistogram().getDataPoints().getSize());
        ProtoExponentialHistogramDataPointAdapter protoExponentialHistogramDataPointAdapter =
                protoExponentialHistogramMetricAdapter.getExponentialHistogram()
                        .getDataPoints().getAt(0);

        assertEquals(50, protoExponentialHistogramDataPointAdapter.getTimeUnixNano());
        assertEquals(60, protoExponentialHistogramDataPointAdapter.getStartTimeUnixNano());
        assertEquals(70.70, protoExponentialHistogramDataPointAdapter.getSum());
        assertEquals(80, protoExponentialHistogramDataPointAdapter.getFlags());

    }

    @Test
    void setAndGet_summaryMetric() {
        protoSummaryMetricAdapter
                .setName("new-name")
                .setDescription("new-description")
                .setUnit("new-unit")
                .getSummary().getDataPoints().getAt(0)
                .setTimeUnixNano(50)
                .setStartTimeUnixNano(60)
                .setSum(70.70)
                .setFlags(80);


        assertEquals("new-name", protoSummaryMetricAdapter.getName());
        assertEquals("new-description", protoSummaryMetricAdapter.getDescription());
        assertEquals("new-unit", protoSummaryMetricAdapter.getUnit());
        assertTrue(protoSummaryMetricAdapter.hasSummary());

        assertNotNull(protoSummaryMetricAdapter.getSummary());
        assertNotNull(protoSummaryMetricAdapter.getSummary().getDataPoints());
        assertEquals(1, protoSummaryMetricAdapter.getSummary().getDataPoints().getSize());
        ProtoSummaryDataPointAdapter protoSummaryDataPointAdapter =
                protoSummaryMetricAdapter.getSummary().getDataPoints().getAt(0);

        assertEquals(50, protoSummaryDataPointAdapter.getTimeUnixNano());
        assertEquals(60, protoSummaryDataPointAdapter.getStartTimeUnixNano());
        assertEquals(70.70, protoSummaryDataPointAdapter.getSum());
        assertEquals(80, protoSummaryDataPointAdapter.getFlags());

    }


}