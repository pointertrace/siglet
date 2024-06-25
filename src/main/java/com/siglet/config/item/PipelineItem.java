package com.siglet.config.item;

import java.util.ArrayList;
import java.util.List;

public class PipelineItem<T extends ProcessorItem> extends Item {

    private ArrayItem<T> processors;

    private List<ValueItem<String>>  from = new ArrayList<>();

    private List<ValueItem<String>>  start = new ArrayList<>();


    public ArrayItem<T> getProcessors() {
        return processors;
    }

    public void setProcessors(ArrayItem<T> processors) {
        this.processors = processors;
    }

    public List<ValueItem<String>> getFrom() {
        return from;
    }

    public void setFrom(List<ValueItem<String>> from) {
        this.from = from;
    }

    public void setFromSingleValue(ValueItem<String> from) {
        this.from = List.of(from);
    }

    public List<ValueItem<String>> getStart() {
        return start;
    }

    public void setStart(List<ValueItem<String>> start) {
        this.start = start;
    }

    public void setStartSingleValue(ValueItem<String> start) {
        this.start = List.of(start);
    }

    @Override
    public void afterSetValues() {
        processors.getValue().forEach(p -> p.setPipeline(new ValueItem<>(getLocation(),getName().getValue())));
    }
}
