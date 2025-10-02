package io.github.pointertrace.siglet.impl.adapter.common;

import io.github.pointertrace.siglet.api.signal.Event;
import io.github.pointertrace.siglet.impl.adapter.Adapter;
import io.github.pointertrace.siglet.impl.adapter.AdapterConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.List;

public class ProtoEventAdapter extends Adapter<Span.Event, Span.Event.Builder> implements Event {

    public ProtoEventAdapter() {
        super(AdapterConfig.EVENT_ADAPTER_CONFIG,
                List.of(),
                List.of(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG));

        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
    }



    public long getTimeUnixNano() {
        return getValue(Span.Event::getTimeUnixNano, Span.Event.Builder::getTimeUnixNano);
    }

    public ProtoEventAdapter setTimeUnixNano(long timeUnixNano) {
        setValue(Span.Event.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    public String getName() {
        return getValue(Span.Event::getName, Span.Event.Builder::getName);
    }

    public ProtoEventAdapter setName(String name) {
        setValue(Span.Event.Builder::setName, name);
        return this;
    }

    public int getDroppedAttributesCount() {
        return getValue(Span.Event::getDroppedAttributesCount, Span.Event.Builder::getDroppedAttributesCount);
    }

    public ProtoEventAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        setValue(Span.Event.Builder::setDroppedAttributesCount, droppedAttributesCount);
        return this;
    }

    protected List<KeyValue> getAttributeList() {
        return getValue(Span.Event::getAttributesList, Span.Event.Builder::getAttributesList);
    }


    public ProtoAttributesAdapter getAttributes() {
        return getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,
                this::getAttributeList);
    }

}
