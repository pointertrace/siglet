package com.siglet.container.adapter.metric;

import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoGaugeAdapterTest {

    private Gauge protoGauge;

    private ProtoGaugeAdapter protoGaugeAdapter;

    private NumberDataPoint protoNumberDataPoint;

    private NumberDataPoint protoExtraNumberDataPoint;

    @BeforeEach
    void setUp() {
        protoNumberDataPoint = NumberDataPoint.newBuilder()
                .setTimeUnixNano(1)
                .setStartTimeUnixNano(2)
                .setAsInt(3)
                .build();

        protoGauge = Gauge.newBuilder()
                .addDataPoints(protoNumberDataPoint)
                .build();


        protoGaugeAdapter = new ProtoGaugeAdapter();
        protoGaugeAdapter.recycle(protoGauge);

    }

    @Test
    void get() {
        protoGaugeAdapter.getDataPoints().get(0);
        assertSame(protoNumberDataPoint, protoGaugeAdapter.getDataPoints().getUpdated().get(0));
        assertFalse(protoGaugeAdapter.isUpdated());
    }

    @Test
    void addNumberDataPoint() {

        protoGaugeAdapter.getDataPoints().add()
                .setTimeUnixNano(100)
                .setStartTimeUnixNano(200)
                .setAsDouble(300.31);

        assertTrue(protoGaugeAdapter.isUpdated());
        ProtoNumberDataPointsAdapter dataPoints = protoGaugeAdapter.getDataPoints();
        assertNotNull(dataPoints);
        assertEquals(2, dataPoints.getSize());

        protoExtraNumberDataPoint = NumberDataPoint.newBuilder()
                .setTimeUnixNano(100)
                .setStartTimeUnixNano(200)
                .setAsDouble(300.31)
                .build();
        assertEquals(protoExtraNumberDataPoint, dataPoints.getUpdated().get(1));
    }

    @Test
    void removeDataPoint() {

        protoGaugeAdapter.getDataPoints().remove(0);

        assertTrue(protoGaugeAdapter.isUpdated());
        ProtoNumberDataPointsAdapter dataPoints = protoGaugeAdapter.getDataPoints();
        assertNotNull(dataPoints);
        assertEquals(0, dataPoints.getSize());

        assertTrue(dataPoints.getUpdated().isEmpty());

    }

    @Test
    void get_updatableNotUpdated() {

        protoGaugeAdapter = new ProtoGaugeAdapter();
        protoGaugeAdapter.recycle(protoGauge);


        protoGaugeAdapter.getDataPoints().get(0);
        assertSame(protoNumberDataPoint, protoGaugeAdapter.getDataPoints().getUpdated().get(0));
        assertSame(protoGauge, protoGaugeAdapter.getUpdated());
        assertFalse(protoGaugeAdapter.isUpdated());

    }

}