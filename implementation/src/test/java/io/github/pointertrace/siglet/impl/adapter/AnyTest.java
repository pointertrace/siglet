package io.github.pointertrace.siglet.impl.adapter;

import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public class AnyTest {


//    @Test
    void shouldReturnSameContainedAdapterInstances() {
        Status status = Status.newBuilder().setCode(Status.StatusCode.STATUS_CODE_OK).build();
        Span.Link link = Span.Link.newBuilder().setTraceState("state").build();
        Span.Event event = Span.Event.newBuilder().setName("evt").build();
        Span span = Span.newBuilder()
                .setName("original")
                .addAllAttributes(List.of(KeyValue.newBuilder().setKey("key").setValue(AnyValue.newBuilder().setStringValue("value").build()).build()))
                .setStatus(status)
                .addLinks(link)
                .addEvents(event)
                .build();
        List<KeyValue> attributes = span.getAttributesList();
        List<Span.Link> links = span.getLinksList();
        List<Span.Event> events = span.getEventsList();
        Resource resource = Resource.getDefaultInstance();
        InstrumentationScope scope = InstrumentationScope.getDefaultInstance();

        SpanAdapter adapter = new SpanAdapter(span, resource, scope);
        adapter.getAttributes().set("key", "value");

        assertSame(status, adapter.getUpdated().getStatus());
        assertSame(attributes, adapter.getUpdated().getAttributesList());
        assertSame(links, adapter.getUpdated().getLinksList());
        assertSame(events, adapter.getUpdated().getEventsList());
        assertSame(resource, adapter.getUpdatedResource());
        assertSame(scope, adapter.getUpdatedScope());
    }

}
