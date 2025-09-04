package io.github.pointertrace.siglet.container.adapter.metric;

import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoValueAtQuantileAdapterTest {

    private SummaryDataPoint.ValueAtQuantile protoValueAtQuantile;

    private ProtoValueAtQuantileAdapter protoValueAtQuantileAdapter;

    @BeforeEach
    void setUp() {

        protoValueAtQuantile = SummaryDataPoint.ValueAtQuantile.newBuilder()
                .setValue(1.2)
                .setQuantile(3.4)
                .build();

        protoValueAtQuantileAdapter =  new ProtoValueAtQuantileAdapter();
        protoValueAtQuantileAdapter.recycle(protoValueAtQuantile);

    }

    @Test
    void get(){

        assertEquals(3.4, protoValueAtQuantileAdapter.getQuantile());
        assertEquals(1.2,protoValueAtQuantileAdapter.getValue());
        assertFalse(protoValueAtQuantileAdapter.isUpdated());

    }

    @Test
    void setAndGet(){

        protoValueAtQuantileAdapter.setQuantile(5.6);
        protoValueAtQuantileAdapter.setValue(7.8);
        assertEquals(5.6, protoValueAtQuantileAdapter.getQuantile());
        assertEquals(7.8, protoValueAtQuantileAdapter.getValue());
        assertTrue(protoValueAtQuantileAdapter.isUpdated());

    }

}