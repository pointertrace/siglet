package io.github.pointertrace.siglet.container.adapter.metric;

import io.opentelemetry.proto.metrics.v1.Summary;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoSummaryAdapterTest {

    private Summary protoSummary;

    private ProtoSummaryAdapter protoSummaryAdapter;

    private SummaryDataPoint protoSummaryDataPoint;

    private SummaryDataPoint protoExtraSummaryDataPoint;

    @BeforeEach
    void setUp() {
        protoSummaryDataPoint = SummaryDataPoint.newBuilder()
                .setTimeUnixNano(1)
                .setStartTimeUnixNano(2)
                .build();

        protoSummary = Summary.newBuilder()
                .addDataPoints(protoSummaryDataPoint)
                .build();


        protoSummaryAdapter = new ProtoSummaryAdapter();
        protoSummaryAdapter.recycle(protoSummary);

    }

    @Test
    void get() {
        protoSummaryAdapter.getDataPoints().get(0);
        assertSame(protoSummaryDataPoint, protoSummaryAdapter.getDataPoints().getUpdated().get(0));
        assertFalse(protoSummaryAdapter.isUpdated());
    }

    @Test
    void addNumberDataPoint() {

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
        Assertions.assertEquals(protoExtraSummaryDataPoint, dataPoints.getUpdated().get(1));
    }

    @Test
    void removeDataPoint() {

        protoSummaryAdapter.getDataPoints().remove(0);

        assertTrue(protoSummaryAdapter.isUpdated());
        ProtoSummaryDataPointsAdapter dataPoints = protoSummaryAdapter.getDataPoints();
        assertNotNull(dataPoints);
        assertEquals(0, dataPoints.getSize());

        Assertions.assertTrue(dataPoints.getUpdated().isEmpty());

    }

    @Test
    void get_updatableNotUpdated() {

        protoSummaryAdapter = new ProtoSummaryAdapter();
        protoSummaryAdapter.recycle(protoSummary);


        protoSummaryAdapter.getDataPoints().get(0);
        assertSame(protoSummaryDataPoint, protoSummaryAdapter.getDataPoints().getUpdated().get(0));
        assertSame(protoSummary, protoSummaryAdapter.getUpdated());
        assertFalse(protoSummaryAdapter.isUpdated());

    }


}