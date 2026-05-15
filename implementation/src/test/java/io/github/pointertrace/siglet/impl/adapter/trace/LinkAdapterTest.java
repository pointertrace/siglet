package io.github.pointertrace.siglet.impl.adapter.trace;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinkAdapterTest {

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.trace.v1.Span.Link link = io.opentelemetry.proto.trace.v1.Span.Link.getDefaultInstance();
        LinkAdapter adapter = new LinkAdapter(link);
        assertSame(link, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.trace.v1.Span.Link link = io.opentelemetry.proto.trace.v1.Span.Link.newBuilder()
            .addAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
            .build();
        List<KeyValue> attributes = link.getAttributesList();

        LinkAdapter adapter = new LinkAdapter(link);

        assertSame(attributes, adapter.getUpdated().getAttributesList());
    }

    @Test
    void shouldRejectNullLink() {
        assertThrows(NullPointerException.class, () -> new LinkAdapter(null));
    }
}
