package io.github.pointertrace.siglet.impl.adapter.metric;

import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoSummaryDataPointsAdapterTest {

    private List<SummaryDataPoint> protoSummaryDataPoints;

    private SummaryDataPoint firstSummaryDataPoint;

    private SummaryDataPoint secondSummaryDataPoint;

    private SummaryDataPoint thirdSummaryDataPoint;

    private ProtoSummaryDataPointsAdapter protoSummaryDataPointsAdapter;

    @BeforeEach
    public void setUp() {


        firstSummaryDataPoint = SummaryDataPoint.newBuilder()
                .setStartTimeUnixNano(2)
                .setTimeUnixNano(3)
                .build();

        secondSummaryDataPoint = SummaryDataPoint.newBuilder()
                .setStartTimeUnixNano(20)
                .setTimeUnixNano(30)
                .build();


        thirdSummaryDataPoint = SummaryDataPoint.newBuilder()
                .setStartTimeUnixNano(200)
                .setTimeUnixNano(300)
                .build();

        protoSummaryDataPoints = List.of(firstSummaryDataPoint, secondSummaryDataPoint);
        protoSummaryDataPointsAdapter = new ProtoSummaryDataPointsAdapter();
        protoSummaryDataPointsAdapter.recycle(protoSummaryDataPoints);

    }

    @Test
    void size() {

        assertEquals(2, protoSummaryDataPointsAdapter.getSize());
    }

    @Test
    void get_At_notChanged() {

        assertSame(firstSummaryDataPoint, protoSummaryDataPointsAdapter.getUpdated().get(0));
        assertSame(secondSummaryDataPoint, protoSummaryDataPointsAdapter.getUpdated().get(1));

    }

    @Test
    void remove() {
        protoSummaryDataPointsAdapter.remove(0);

        assertEquals(1, protoSummaryDataPointsAdapter.getSize());
        assertSame(secondSummaryDataPoint, protoSummaryDataPointsAdapter.getUpdated().get(0));

    }

    @Test
    void add_andGetAt() {

        protoSummaryDataPointsAdapter.add()
                .setStartTimeUnixNano(200)
                .setTimeUnixNano(300);
        assertEquals(3, protoSummaryDataPointsAdapter.getSize());
        assertTrue(protoSummaryDataPointsAdapter.isUpdated());
        assertSame(firstSummaryDataPoint, protoSummaryDataPointsAdapter.getUpdated().get(0));
        assertSame(secondSummaryDataPoint, protoSummaryDataPointsAdapter.getUpdated().get(1));
        assertEquals(thirdSummaryDataPoint, protoSummaryDataPointsAdapter.getUpdated().get(2));

    }

    @Test
    void get_At_notUpdatable() {

        protoSummaryDataPointsAdapter = new ProtoSummaryDataPointsAdapter();
        protoSummaryDataPointsAdapter.recycle(protoSummaryDataPoints);

        assertEquals(2, protoSummaryDataPointsAdapter.getSize());

        assertSame(firstSummaryDataPoint, protoSummaryDataPointsAdapter.getUpdated().get(0));
        assertSame(secondSummaryDataPoint, protoSummaryDataPointsAdapter.getUpdated().get(1));

    }


}