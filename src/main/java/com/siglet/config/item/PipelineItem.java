package com.siglet.config.item;

import java.util.ArrayList;
import java.util.List;

public class PipelineItem<T extends ProcessorItem> extends Item {

    private List<T> processors = new ArrayList<>();

    private List<String>  from = new ArrayList<>();

    private List<String>  start = new ArrayList<>();


    public List<T> getProcessors() {
        return processors;
    }

    public void setProcessors(List<T> processors) {
        processors.forEach(p -> p.setPipeline(getName()));
        this.processors = processors;
    }

    public List<String> getFrom() {
        return from;
    }

    public void setFrom(List<String> from) {
        this.from = from;
    }

    public void setFromSingleValue(String from) {
        this.from = List.of(from);
    }
    public List<String> getStart() {
        return start;
    }

    public void setStart(List<String> start) {
        this.start = start;
    }

    public void setStartSingleValue(String start) {
        this.start = List.of(start);
    }
}
