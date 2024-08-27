package com.siglet.data.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtoValueAtQuantileAdapterTest {

    private SummaryDataPoint.ValueAtQuantile protoValueAtQuantile;

    private ProtoValueAtQuantileAdapter protoValueAtQuantileAdapter;

    @BeforeEach
    public void setUp() {

        protoValueAtQuantile = SummaryDataPoint.ValueAtQuantile.newBuilder()
                .setValue(1.2)
                .setQuantile(3.4)
                .build();

        protoValueAtQuantileAdapter =  new ProtoValueAtQuantileAdapter(protoValueAtQuantile, true);

    }

    @Test
    public void get(){

        assertEquals(3.4, protoValueAtQuantileAdapter.getQuantile());
        assertEquals(1.2,protoValueAtQuantileAdapter.getValue());
        assertFalse(protoValueAtQuantileAdapter.isUpdated());

    }

    @Test
    public void setAndGet(){

        protoValueAtQuantileAdapter.setQuantile(5.6);
        protoValueAtQuantileAdapter.setValue(7.8);
        assertEquals(5.6, protoValueAtQuantileAdapter.getQuantile());
        assertEquals(7.8, protoValueAtQuantileAdapter.getValue());
        assertTrue(protoValueAtQuantileAdapter.isUpdated());

    }

    @Test
    public void changeNotUpdated(){

        protoValueAtQuantileAdapter = new ProtoValueAtQuantileAdapter(protoValueAtQuantile, false);

        assertThrowsExactly(SigletError.class, () -> protoValueAtQuantileAdapter.setQuantile(0));

        assertThrowsExactly(SigletError.class, () -> protoValueAtQuantileAdapter.setValue(0));
        
    }

}