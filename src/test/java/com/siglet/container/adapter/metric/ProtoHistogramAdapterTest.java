package com.siglet.container.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.AggregationTemporality;
import io.opentelemetry.proto.metrics.v1.Histogram;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoHistogramAdapterTest {


    private Histogram protoHistogram;

    private ProtoHistogramAdapter protoHistogramAdapter;

    private HistogramDataPoint protoNumberDataPoint;

    private HistogramDataPoint protoExtraNumberDataPoint;

    @BeforeEach
    public void setUp() {
        protoNumberDataPoint = HistogramDataPoint.newBuilder()
                .setTimeUnixNano(1)
                .setStartTimeUnixNano(2)
                .build();

        protoHistogram = Histogram.newBuilder()
                .addDataPoints(protoNumberDataPoint)
                .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE)
                .build();


        protoHistogramAdapter = new ProtoHistogramAdapter(protoHistogram, true);

    }

    @Test
    void get() {
        protoHistogramAdapter.getDataPoints().getAt(0);
        assertSame(protoNumberDataPoint, protoHistogramAdapter.getDataPoints().getUpdated().get(0));
        assertFalse(protoHistogramAdapter.isUpdated());
    }

    @Test
    void update_andGet() {

        protoHistogramAdapter.getDataPoints().add()
                .setTimeUnixNano(10)
                .setStartTimeUnixNano(20);

        protoExtraNumberDataPoint = HistogramDataPoint.newBuilder()
                .setTimeUnixNano(10)
                .setStartTimeUnixNano(20)
                .build();

        new ProtoHistogramDataPointAdapter(protoExtraNumberDataPoint, true);

        assertSame(protoNumberDataPoint, protoHistogramAdapter.getDataPoints().getUpdated().get(0));
        assertEquals(protoExtraNumberDataPoint, protoHistogramAdapter.getDataPoints().getUpdated().get(1));
        assertTrue(protoHistogramAdapter.isUpdated());

    }

    @Test
    void get_updatableNotUpdated() {

        protoHistogramAdapter = new ProtoHistogramAdapter(protoHistogram, false);


        protoHistogramAdapter.getDataPoints().getAt(0);
        assertSame(protoNumberDataPoint, protoHistogramAdapter.getDataPoints().getUpdated().get(0));
        assertSame(protoHistogram, protoHistogramAdapter.getUpdated());
        assertFalse(protoHistogramAdapter.isUpdated());

    }

    @Test
    void change_notUpdatable() {

        protoHistogramAdapter = new ProtoHistogramAdapter(protoHistogram, false);


        assertThrowsExactly(SigletError.class, () -> protoHistogramAdapter.getDataPoints().remove(0));
        assertThrowsExactly(SigletError.class, () -> protoHistogramAdapter.getDataPoints().add());

    }

}