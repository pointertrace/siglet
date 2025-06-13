package com.siglet.container.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoNumberDataPointsAdapterTest {

    private List<NumberDataPoint> protoNumberDataPoints;

    private NumberDataPoint firstNumberDataPoint;

    private NumberDataPoint secondNumberDataPoint;

    private NumberDataPoint thirdNumberDataPoint;

    private ProtoNumberDataPointsAdapter protoNumberDataPointsAdapter;

    private ProtoNumberDataPointAdapter protoThirdNumberDataPointAdapter;

    @BeforeEach
    public void setUp() {


        firstNumberDataPoint = NumberDataPoint.newBuilder()
                .setAsInt(1)
                .setStartTimeUnixNano(2)
                .setTimeUnixNano(3)
                .build();

        secondNumberDataPoint = NumberDataPoint.newBuilder()
                .setAsInt(10)
                .setStartTimeUnixNano(20)
                .setTimeUnixNano(30)
                .build();


        thirdNumberDataPoint = NumberDataPoint.newBuilder()
                .setAsInt(100)
                .setStartTimeUnixNano(200)
                .setTimeUnixNano(300)
                .build();

        protoNumberDataPoints = List.of(firstNumberDataPoint, secondNumberDataPoint);
        protoNumberDataPointsAdapter = new ProtoNumberDataPointsAdapter(protoNumberDataPoints, true);

        protoThirdNumberDataPointAdapter =
                new ProtoNumberDataPointAdapter(thirdNumberDataPoint, true);
    }

    @Test
    void size() {

        assertEquals(2, protoNumberDataPointsAdapter.getSize());
    }

    @Test
    void get_At_notChanged() {

        assertSame(firstNumberDataPoint, protoNumberDataPointsAdapter.getUpdated().get(0));
        assertSame(secondNumberDataPoint, protoNumberDataPointsAdapter.getUpdated().get(1));

    }

    @Test
    void remove() {
        protoNumberDataPointsAdapter.remove(0);

        assertEquals(1, protoNumberDataPointsAdapter.getSize());
        assertSame(secondNumberDataPoint, protoNumberDataPointsAdapter.getUpdated().get(0));

    }

    @Test
    void add_andGetAt() {

        protoNumberDataPointsAdapter.add()
                .setAsLong(100)
                .setStartTimeUnixNano(200)
                .setTimeUnixNano(300);
        assertEquals(3, protoNumberDataPointsAdapter.getSize());
        assertTrue(protoNumberDataPointsAdapter.isUpdated());
        assertSame(firstNumberDataPoint, protoNumberDataPointsAdapter.getUpdated().get(0));
        assertSame(secondNumberDataPoint, protoNumberDataPointsAdapter.getUpdated().get(1));
        assertEquals(thirdNumberDataPoint, protoNumberDataPointsAdapter.getUpdated().get(2));

    }

    @Test
    void get_At_notUpdatable() {

        protoNumberDataPointsAdapter = new ProtoNumberDataPointsAdapter(protoNumberDataPoints, false);

        assertEquals(2, protoNumberDataPointsAdapter.getSize());

        assertSame(firstNumberDataPoint, protoNumberDataPointsAdapter.getUpdated().get(0));
        assertSame(secondNumberDataPoint, protoNumberDataPointsAdapter.getUpdated().get(1));

    }

    @Test
    void update_notUpdatable() {

        protoNumberDataPointsAdapter = new ProtoNumberDataPointsAdapter(protoNumberDataPoints, false);

        assertThrowsExactly(SigletError.class, () -> protoNumberDataPointsAdapter.add());
        assertThrowsExactly(SigletError.class, () -> protoNumberDataPointsAdapter.remove(0));

    }

}