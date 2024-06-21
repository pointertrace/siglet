package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoGaugeAdapterTest {

    private Gauge protoGauge;

    private ProtoGaugeAdapter protoGaugeAdapter;

    private NumberDataPoint protoNumberDataPoint;

    private NumberDataPoint protoExtraNumberDataPoint;

    private ProtoNumberDataPointsAdapter protoNumberDataPointsAdapter;

    @BeforeEach
    public void setUp() {
        protoNumberDataPoint = NumberDataPoint.newBuilder()
                .setTimeUnixNano(1)
                .setStartTimeUnixNano(2)
                .setAsInt(3)
                .build();

        protoExtraNumberDataPoint = NumberDataPoint.newBuilder()
                .setTimeUnixNano(10)
                .setStartTimeUnixNano(20)
                .setAsDouble(30.21)
                .build();

        protoGauge = Gauge.newBuilder()
                .addDataPoints(protoNumberDataPoint)
                .build();


        protoNumberDataPointsAdapter = new ProtoNumberDataPointsAdapter(List.of(protoNumberDataPoint), true);

        protoGaugeAdapter = new ProtoGaugeAdapter(protoGauge, true);

    }

    @Test
    public void get() {
        protoGaugeAdapter.getDataPoints().get(0);
        assertSame(protoNumberDataPoint, protoGaugeAdapter.getDataPoints().getUpdated().get(0));
        assertFalse(protoGaugeAdapter.isUpdated());
    }

    @Test
    public void update_andGet() {

        protoGaugeAdapter.getDataPoints().add(new ProtoNumberDataPointAdapter(protoExtraNumberDataPoint, true));

        assertSame(protoNumberDataPoint, protoGaugeAdapter.getDataPoints().getUpdated().get(0));
        assertSame(protoExtraNumberDataPoint, protoGaugeAdapter.getDataPoints().getUpdated().get(1));
        assertTrue(protoGaugeAdapter.isUpdated());

    }

    @Test
    public void get_updatableNotUpdated() {

        protoGaugeAdapter = new ProtoGaugeAdapter(protoGauge, false);


        protoGaugeAdapter.getDataPoints().get(0);
        assertSame(protoNumberDataPoint, protoGaugeAdapter.getDataPoints().getUpdated().get(0));
        assertSame(protoGauge, protoGaugeAdapter.getUpdatedGauge());
        assertFalse(protoGaugeAdapter.isUpdated());

    }

    @Test
    public void change_notUpdatable() {

        protoGaugeAdapter = new ProtoGaugeAdapter(protoGauge, false);


        assertThrowsExactly(SigletError.class,() -> protoGaugeAdapter.getDataPoints().remove(0));
        assertThrowsExactly(SigletError.class,() -> protoGaugeAdapter.getDataPoints().add(
                new ProtoNumberDataPointAdapter(protoExtraNumberDataPoint, false)));

    }

}