package com.siglet.container.adapter.metric;

import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ProtoExponentialHistogramDataPointsAdapterTest {

    private List<ExponentialHistogramDataPoint> protoExponentialHistogramDataPoints;

    private ExponentialHistogramDataPoint firstExponentialHistogramDataPoint;

    private ExponentialHistogramDataPoint secondExponentialHistogramDataPoint;

    private ExponentialHistogramDataPoint thirdExponentialHistogramDataPoint;

    private ProtoExponentialHistogramDataPointsAdapter protoExponentialHistogramDataPointsAdapter;

    @BeforeEach
    public void setUp() {


        firstExponentialHistogramDataPoint = ExponentialHistogramDataPoint.newBuilder()
                .setStartTimeUnixNano(2)
                .setTimeUnixNano(3)
                .build();

        secondExponentialHistogramDataPoint = ExponentialHistogramDataPoint.newBuilder()
                .setStartTimeUnixNano(20)
                .setTimeUnixNano(30)
                .build();


        thirdExponentialHistogramDataPoint = ExponentialHistogramDataPoint.newBuilder()
                .setStartTimeUnixNano(200)
                .setTimeUnixNano(300)
                .build();

        protoExponentialHistogramDataPoints = List.of(firstExponentialHistogramDataPoint,
                secondExponentialHistogramDataPoint);

        protoExponentialHistogramDataPointsAdapter = new ProtoExponentialHistogramDataPointsAdapter();
        protoExponentialHistogramDataPointsAdapter.recycle(protoExponentialHistogramDataPoints);

    }

    @Test
    void size() {

        assertEquals(2, protoExponentialHistogramDataPointsAdapter.getSize());
    }

    @Test
    void get_At_notChanged() {

        assertSame(firstExponentialHistogramDataPoint, protoExponentialHistogramDataPointsAdapter.getUpdated().get(0));
        assertSame(secondExponentialHistogramDataPoint, protoExponentialHistogramDataPointsAdapter.getUpdated().get(1));

    }

    @Test
    void remove() {
        protoExponentialHistogramDataPointsAdapter.remove(0);

        assertEquals(1, protoExponentialHistogramDataPointsAdapter.getSize());
        assertSame(secondExponentialHistogramDataPoint, protoExponentialHistogramDataPointsAdapter.getUpdated().getFirst());

    }

    @Test
    void add_andGetAt() {

        protoExponentialHistogramDataPointsAdapter.add()
                .setStartTimeUnixNano(200)
                .setTimeUnixNano(300);

        assertEquals(3, protoExponentialHistogramDataPointsAdapter.getSize());
        Assertions.assertTrue(protoExponentialHistogramDataPointsAdapter.isUpdated());
        assertSame(firstExponentialHistogramDataPoint, protoExponentialHistogramDataPointsAdapter.getUpdated().get(0));
        assertSame(secondExponentialHistogramDataPoint, protoExponentialHistogramDataPointsAdapter.getUpdated().get(1));
        Assertions.assertEquals(thirdExponentialHistogramDataPoint, protoExponentialHistogramDataPointsAdapter.getUpdated().get(2));

    }

    @Test
    void get_At_notUpdatable() {

        protoExponentialHistogramDataPointsAdapter = new ProtoExponentialHistogramDataPointsAdapter();
        protoExponentialHistogramDataPointsAdapter.recycle(protoExponentialHistogramDataPoints);

        assertEquals(2, protoExponentialHistogramDataPointsAdapter.getSize());

        assertSame(firstExponentialHistogramDataPoint, protoExponentialHistogramDataPointsAdapter.getUpdated().get(0));
        assertSame(secondExponentialHistogramDataPoint, protoExponentialHistogramDataPointsAdapter.getUpdated().get(1));

    }


}