package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.Summary;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoSummaryAdapterTest {

    private Summary protoSummary;

    private ProtoSummaryAdapter protoSummaryAdapter;

    private SummaryDataPoint protoSummaryDataPoint;

    private SummaryDataPoint protoExtraSummaryDataPoint;

    @BeforeEach
    public void setUp() {
        protoSummaryDataPoint = SummaryDataPoint.newBuilder()
                .setTimeUnixNano(1)
                .setStartTimeUnixNano(2)
                .build();

        protoSummary = Summary.newBuilder()
                .addDataPoints(protoSummaryDataPoint)
                .build();


        protoSummaryAdapter = new ProtoSummaryAdapter(protoSummary, true);

    }

    @Test
    public void get() {
        protoSummaryAdapter.getDataPoints().getAt(0);
        assertSame(protoSummaryDataPoint, protoSummaryAdapter.getDataPoints().getUpdated().get(0));
        assertFalse(protoSummaryAdapter.isUpdated());
    }

    @Test
    public void addNumberDataPoint() {

        protoSummaryAdapter.getDataPoints().add()
                .setTimeUnixNano(100)
                .setStartTimeUnixNano(200);

        assertTrue(protoSummaryAdapter.isUpdated());

        ProtoSummaryDataPointsAdapter dataPoints = protoSummaryAdapter.getDataPoints();
        assertNotNull(dataPoints);
        assertEquals(2, dataPoints.getSize());

        protoExtraSummaryDataPoint = SummaryDataPoint.newBuilder()
                .setTimeUnixNano(100)
                .setStartTimeUnixNano(200)
                .build();
        assertEquals(protoExtraSummaryDataPoint, dataPoints.getUpdated().get(1));
    }

    @Test
    public void removeDataPoint() {

        protoSummaryAdapter.getDataPoints().remove(0);

        assertTrue(protoSummaryAdapter.isUpdated());
        ProtoSummaryDataPointsAdapter dataPoints = protoSummaryAdapter.getDataPoints();
        assertNotNull(dataPoints);
        assertEquals(0, dataPoints.getSize());

        assertTrue(dataPoints.getUpdated().isEmpty());

    }

    @Test
    public void get_updatableNotUpdated() {

        protoSummaryAdapter = new ProtoSummaryAdapter(protoSummary, false);


        protoSummaryAdapter.getDataPoints().getAt(0);
        assertSame(protoSummaryDataPoint, protoSummaryAdapter.getDataPoints().getUpdated().get(0));
        assertSame(protoSummary, protoSummaryAdapter.getUpdated());
        assertFalse(protoSummaryAdapter.isUpdated());

    }

    @Test
    public void change_notUpdatable() {

        protoSummaryAdapter = new ProtoSummaryAdapter(protoSummary, false);


        assertThrowsExactly(SigletError.class, () -> protoSummaryAdapter.getDataPoints().remove(0));
        assertThrowsExactly(SigletError.class, () -> protoSummaryAdapter.getDataPoints().add());

    }

}