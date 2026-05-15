package io.github.pointertrace.siglet.impl.adapter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceAdapterTest {
    @Test
    void shouldRejectNullResource() {
        assertThrows(NullPointerException.class, () -> new ResourceAdapter(null));
    }

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {
        io.opentelemetry.proto.resource.v1.Resource resource = io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance();
        ResourceAdapter adapter = new ResourceAdapter(resource);
        assertSame(resource, adapter.getUpdated());
    }

    @Test
    void shouldReturnSameContainedListInstances() {
        io.opentelemetry.proto.resource.v1.Resource resource = io.opentelemetry.proto.resource.v1.Resource.newBuilder()
            .addAttributes(KeyValue.newBuilder().setKey("k").setValue(AnyValue.newBuilder().setStringValue("v").build()).build())
            .build();
        List<KeyValue> attributes = resource.getAttributesList();

        ResourceAdapter adapter = new ResourceAdapter(resource);

        assertSame(resource, adapter.getUpdated());
        assertSame(attributes, adapter.getUpdated().getAttributesList());
    }
}
