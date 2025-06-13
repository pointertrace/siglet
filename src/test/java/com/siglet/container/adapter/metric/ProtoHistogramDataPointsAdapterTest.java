package com.siglet.container.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoHistogramDataPointsAdapterTest {

    private List<HistogramDataPoint> protoHistogramDataPoints;

    private HistogramDataPoint firstHistogramDataPoint;

    private HistogramDataPoint secondHistogramDataPoint;

    private HistogramDataPoint thirdHistogramDataPoint;

    private ProtoHistogramDataPointsAdapter protoHistogramDataPointsAdapter;

    private ProtoHistogramDataPointAdapter protoThirdNumberDataPointAdapter;

    @BeforeEach
    public void setUp() {


        firstHistogramDataPoint = HistogramDataPoint.newBuilder()
                .setStartTimeUnixNano(2)
                .setTimeUnixNano(3)
                .build();

        secondHistogramDataPoint = HistogramDataPoint.newBuilder()
                .setStartTimeUnixNano(20)
                .setTimeUnixNano(30)
                .build();


        thirdHistogramDataPoint = HistogramDataPoint.newBuilder()
                .setStartTimeUnixNano(200)
                .setTimeUnixNano(300)
                .build();

        protoHistogramDataPoints = List.of(firstHistogramDataPoint, secondHistogramDataPoint);
        protoHistogramDataPointsAdapter = new ProtoHistogramDataPointsAdapter(protoHistogramDataPoints, true);

        protoThirdNumberDataPointAdapter =
                new ProtoHistogramDataPointAdapter(thirdHistogramDataPoint, true);
    }

    @Test
    void size() {

        assertEquals(2, protoHistogramDataPointsAdapter.getSize());
    }

    @Test
    void get_At_notChanged() {

        assertSame(firstHistogramDataPoint, protoHistogramDataPointsAdapter.getUpdated().get(0));
        assertSame(secondHistogramDataPoint, protoHistogramDataPointsAdapter.getUpdated().get(1));

    }

    @Test
    void remove() {
        protoHistogramDataPointsAdapter.remove(0);

        assertEquals(1, protoHistogramDataPointsAdapter.getSize());
        assertSame(secondHistogramDataPoint, protoHistogramDataPointsAdapter.getUpdated().get(0));

    }

    @Test
    void add_andGetAt() {

        protoHistogramDataPointsAdapter.add()
                .setStartTimeUnixNano(200)
                .setTimeUnixNano(300);
        assertEquals(3, protoHistogramDataPointsAdapter.getSize());
        assertTrue(protoHistogramDataPointsAdapter.isUpdated());
        assertSame(firstHistogramDataPoint, protoHistogramDataPointsAdapter.getUpdated().get(0));
        assertSame(secondHistogramDataPoint, protoHistogramDataPointsAdapter.getUpdated().get(1));
        assertEquals(thirdHistogramDataPoint, protoHistogramDataPointsAdapter.getUpdated().get(2));

    }

    @Test
    void get_At_notUpdatable() {

        protoHistogramDataPointsAdapter = new ProtoHistogramDataPointsAdapter(protoHistogramDataPoints, false);

        assertEquals(2, protoHistogramDataPointsAdapter.getSize());

        assertSame(firstHistogramDataPoint, protoHistogramDataPointsAdapter.getUpdated().get(0));
        assertSame(secondHistogramDataPoint, protoHistogramDataPointsAdapter.getUpdated().get(1));

    }

    @Test
    void update_notUpdatable() {

        protoHistogramDataPointsAdapter = new ProtoHistogramDataPointsAdapter(protoHistogramDataPoints, false);

        assertThrowsExactly(SigletError.class, () -> protoHistogramDataPointsAdapter.add());
        assertThrowsExactly(SigletError.class, () -> protoHistogramDataPointsAdapter.remove(0));

    }

}