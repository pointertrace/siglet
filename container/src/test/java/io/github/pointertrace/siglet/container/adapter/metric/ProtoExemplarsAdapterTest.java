package io.github.pointertrace.siglet.container.adapter.metric;

import io.github.pointertrace.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import org.junit.jupiter.api.Assertions;
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
    void setUp() {

        protoExemplars = new ArrayList<>();

        firstProtoExemplar = Exemplar.newBuilder()
                .setAsInt(10)
                .setTraceId(AdapterUtils.traceId(1, 2))
                .setSpanId(AdapterUtils.spanId(3))
                .build();

        protoExemplars.add(firstProtoExemplar);

        secondProtoExemplar = Exemplar.newBuilder()
                .setAsInt(20)
                .setTraceId(AdapterUtils.traceId(4, 5))
                .setSpanId(AdapterUtils.spanId(6))
                .build();

        protoExemplars.add(secondProtoExemplar);

        protoExemplarsAdapter = new ProtoExemplarsAdapter();
        protoExemplarsAdapter.recycle(protoExemplars);
    }

    @Test
    void size() {
        assertEquals(2, protoExemplarsAdapter.getSize());
        Assertions.assertFalse(protoExemplarsAdapter.isUpdated());
    }

    @Test
    void getUpdated_notChanged() {
        assertSame(firstProtoExemplar, protoExemplarsAdapter.getUpdated().get(0));
        assertSame(secondProtoExemplar, protoExemplarsAdapter.getUpdated().get(1));
    }

    @Test
    void get() {
        assertNotNull(protoExemplarsAdapter.get(0));
        Assertions.assertEquals(AdapterUtils.traceId(1, 2),
                protoExemplarsAdapter.getUpdated().getFirst().getTraceId());
        Assertions.assertEquals(AdapterUtils.spanId(3),
                protoExemplarsAdapter.getUpdated().getFirst().getSpanId());
    }

    @Test
    void remove() {
        protoExemplarsAdapter.remove(0);

        assertEquals(1, protoExemplarsAdapter.getSize());
        assertSame(secondProtoExemplar, protoExemplarsAdapter.getUpdated().getFirst());
        Assertions.assertTrue(protoExemplarsAdapter.isUpdated());

    }

    @Test
    void add_andGet() {

        protoExemplarsAdapter.add()
                .setAsLong(30)
                .setTraceId(7, 8)
                .setSpanId(9);

        assertEquals(3, protoExemplarsAdapter.getSize());
        Assertions.assertTrue(protoExemplarsAdapter.isUpdated());
        assertSame(firstProtoExemplar, protoExemplarsAdapter.getUpdated().get(0));
        assertSame(secondProtoExemplar, protoExemplarsAdapter.getUpdated().get(1));
        Assertions.assertEquals(30, protoExemplarsAdapter.getUpdated().get(2).getAsInt());
        Assertions.assertEquals(AdapterUtils.traceId(7, 8),
                protoExemplarsAdapter.getUpdated().get(2).getTraceId());
        Assertions.assertEquals(AdapterUtils.spanId(9),
                protoExemplarsAdapter.getUpdated().get(2).getSpanId());


    }

    @Test
    void get_notUpdatable() {
        protoExemplarsAdapter = new ProtoExemplarsAdapter();
        protoExemplarsAdapter.recycle(protoExemplars);

        assertSame(firstProtoExemplar, protoExemplarsAdapter.getUpdated().get(0));
        assertSame(secondProtoExemplar, protoExemplarsAdapter.getUpdated().get(1));

    }


}