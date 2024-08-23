package com.siglet.data.adapter.metric;

import com.google.protobuf.ByteString;
import com.siglet.SigletError;
import com.siglet.data.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoExemplarsAdapterTest {

    private List<Exemplar> protoExemplars;

    private Exemplar firstProtoExemplar;

    private Exemplar secondProtoExemplar;

    private ProtoExemplarsAdapter protoExemplarsAdapter;


    @BeforeEach
    public void setUp() {

        protoExemplars = new ArrayList<>();

        firstProtoExemplar = Exemplar.newBuilder()
                .setAsInt(10)
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(1, 2)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(3)))
                .build();

        protoExemplars.add(firstProtoExemplar);

        secondProtoExemplar = Exemplar.newBuilder()
                .setAsInt(20)
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(4, 5)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(6)))
                .build();

        protoExemplars.add(secondProtoExemplar);

        protoExemplarsAdapter = new ProtoExemplarsAdapter(protoExemplars, true);
    }

    @Test
    public void size() {
        assertEquals(2, protoExemplarsAdapter.getSize());
        assertFalse(protoExemplarsAdapter.isUpdated());
    }

    @Test
    public void getUpdated_notChanged() {
        assertSame(firstProtoExemplar, protoExemplarsAdapter.getUpdated().get(0));
        assertSame(secondProtoExemplar, protoExemplarsAdapter.getUpdated().get(1));
    }

    @Test
    public void get() {
        assertNotNull(protoExemplarsAdapter.get(0));
        assertArrayEquals(AdapterUtils.traceId(1, 2),
                protoExemplarsAdapter.getUpdated().get(0).getTraceId().toByteArray());
        assertArrayEquals(AdapterUtils.spanId(3),
                protoExemplarsAdapter.getUpdated().get(0).getSpanId().toByteArray());
    }

    @Test
    public void remove() {
        protoExemplarsAdapter.remove(0);

        assertEquals(1, protoExemplarsAdapter.getSize());
        assertSame(secondProtoExemplar, protoExemplarsAdapter.getUpdated().get(0));
        assertTrue(protoExemplarsAdapter.isUpdated());

    }

    @Test
    public void add_andGet() {

        protoExemplarsAdapter.add()
                .setAsLong(30)
                .setTraceId(7, 8)
                .setSpanId(9);

        assertEquals(3, protoExemplarsAdapter.getSize());
        assertTrue(protoExemplarsAdapter.isUpdated());
        assertSame(firstProtoExemplar, protoExemplarsAdapter.getUpdated().get(0));
        assertSame(secondProtoExemplar, protoExemplarsAdapter.getUpdated().get(1));
        assertEquals(30, protoExemplarsAdapter.getUpdated().get(2).getAsInt());
        assertArrayEquals(AdapterUtils.traceId(7, 8),
                protoExemplarsAdapter.getUpdated().get(2).getTraceId().toByteArray());
        assertArrayEquals(AdapterUtils.spanId(9),
                protoExemplarsAdapter.getUpdated().get(2).getSpanId().toByteArray());


    }

    @Test
    public void get_notUpdatable() {
        protoExemplarsAdapter = new ProtoExemplarsAdapter(protoExemplars, false);

        assertSame(firstProtoExemplar, protoExemplarsAdapter.getUpdated().get(0));
        assertSame(secondProtoExemplar, protoExemplarsAdapter.getUpdated().get(1));

    }

    @Test
    public void update_notUpdatable() {
        protoExemplarsAdapter = new ProtoExemplarsAdapter(protoExemplars, false);

        assertThrowsExactly(SigletError.class, () -> protoExemplarsAdapter.add());
        assertThrowsExactly(SigletError.class, () -> protoExemplarsAdapter.remove(0));

    }

}