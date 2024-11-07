package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.AggregationTemporality;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.Sum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoSumAdapterTest {

    private Sum protoSum;

    private ProtoSumAdapter protoSumAdapter;

    private NumberDataPoint protoNumberDataPoint;

    private NumberDataPoint protoExtraNumberDataPoint;

    @BeforeEach
    public void setUp() {
        protoNumberDataPoint = NumberDataPoint.newBuilder()
                .setTimeUnixNano(1)
                .setStartTimeUnixNano(2)
                .setAsInt(3)
                .build();

        protoSum = Sum.newBuilder()
                .addDataPoints(protoNumberDataPoint)
                .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE)
                .setIsMonotonic(false)
                .build();


        protoSumAdapter = new ProtoSumAdapter(protoSum, true);

    }

    @Test
    public void get() {
        protoSumAdapter.getDataPoints().getAt(0);
        assertSame(protoNumberDataPoint, protoSumAdapter.getDataPoints().getUpdated().get(0));
        assertFalse(protoSumAdapter.isUpdated());
    }

    @Test
    public void update_andGet() {

        protoSumAdapter.getDataPoints().add()
                .setTimeUnixNano(10)
                .setStartTimeUnixNano(20)
                .setAsDouble(30.21);

        protoExtraNumberDataPoint = NumberDataPoint.newBuilder()
                .setTimeUnixNano(10)
                .setStartTimeUnixNano(20)
                .setAsDouble(30.21)
                .build();

        new ProtoNumberDataPointAdapter(protoExtraNumberDataPoint, true);

        assertSame(protoNumberDataPoint, protoSumAdapter.getDataPoints().getUpdated().get(0));
        assertEquals(protoExtraNumberDataPoint, protoSumAdapter.getDataPoints().getUpdated().get(1));
        assertTrue(protoSumAdapter.isUpdated());

    }

    @Test
    public void get_updatableNotUpdated() {

        protoSumAdapter = new ProtoSumAdapter(protoSum, false);


        protoSumAdapter.getDataPoints().getAt(0);
        assertSame(protoNumberDataPoint, protoSumAdapter.getDataPoints().getUpdated().get(0));
        assertSame(protoSum, protoSumAdapter.getUpdated());
        assertFalse(protoSumAdapter.isUpdated());

    }

    @Test
    public void change_notUpdatable() {

        protoSumAdapter = new ProtoSumAdapter(protoSum, false);


        assertThrowsExactly(SigletError.class, () -> protoSumAdapter.getDataPoints().remove(0));
        assertThrowsExactly(SigletError.class, () -> protoSumAdapter.getDataPoints().add());

    }

}