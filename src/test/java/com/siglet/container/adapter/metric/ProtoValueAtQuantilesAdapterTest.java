package com.siglet.container.adapter.metric;

import com.siglet.SigletError;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoValueAtQuantilesAdapterTest {

    private List<SummaryDataPoint.ValueAtQuantile> protoValueAtQuantile;

    private SummaryDataPoint.ValueAtQuantile firstValueAtQuantile;

    private SummaryDataPoint.ValueAtQuantile secondValueAtQuantile;

    private ProtoValueAtQuantilesAdapter protoValueAtQuantilesAdapter;


    @BeforeEach
    public void setUp() {


        firstValueAtQuantile = SummaryDataPoint.ValueAtQuantile.newBuilder()
                .setQuantile(1.1)
                .setValue(2.2)
                .build();

        secondValueAtQuantile = SummaryDataPoint.ValueAtQuantile.newBuilder()
                .setQuantile(3.3)
                .setValue(4.4)
                .build();


        protoValueAtQuantile = List.of(firstValueAtQuantile, secondValueAtQuantile);
        protoValueAtQuantilesAdapter = new ProtoValueAtQuantilesAdapter(protoValueAtQuantile, true);

    }

    @Test
    void size() {

        assertEquals(2, protoValueAtQuantilesAdapter.getSize());
    }

    @Test
    void get_At_notChanged() {

        assertSame(firstValueAtQuantile, protoValueAtQuantilesAdapter.getUpdated().get(0));
        assertSame(secondValueAtQuantile, protoValueAtQuantilesAdapter.getUpdated().get(1));

    }

    @Test
    void remove() {
        protoValueAtQuantilesAdapter.remove(0);

        assertEquals(1, protoValueAtQuantilesAdapter.getSize());
        assertSame(secondValueAtQuantile, protoValueAtQuantilesAdapter.getUpdated().get(0));

    }

    @Test
    void add_andGetAt() {

        protoValueAtQuantilesAdapter.add()
                .setValue(7.7)
                .setQuantile(8.8);

        assertEquals(3, protoValueAtQuantilesAdapter.getSize());
        assertTrue(protoValueAtQuantilesAdapter.isUpdated());
        assertSame(firstValueAtQuantile, protoValueAtQuantilesAdapter.getUpdated().get(0));
        assertSame(secondValueAtQuantile, protoValueAtQuantilesAdapter.getUpdated().get(1));
        SummaryDataPoint.ValueAtQuantile valueAtQuantile= protoValueAtQuantilesAdapter.getUpdated().get(2);
        assertEquals(7.7,valueAtQuantile.getValue());
        assertEquals(8.8,valueAtQuantile.getQuantile());

    }

    @Test
    void get_At_notUpdatable() {

        protoValueAtQuantilesAdapter = new ProtoValueAtQuantilesAdapter(protoValueAtQuantile, false);

        assertEquals(2, protoValueAtQuantilesAdapter.getSize());

        assertSame(firstValueAtQuantile, protoValueAtQuantilesAdapter.getUpdated().get(0));
        assertSame(secondValueAtQuantile, protoValueAtQuantilesAdapter.getUpdated().get(1));

    }

    @Test
    void update_notUpdatable() {

        protoValueAtQuantilesAdapter = new ProtoValueAtQuantilesAdapter(protoValueAtQuantile, false);

        assertThrowsExactly(SigletError.class, () -> protoValueAtQuantilesAdapter.add());
        assertThrowsExactly(SigletError.class, () -> protoValueAtQuantilesAdapter.remove(0));

    }

}