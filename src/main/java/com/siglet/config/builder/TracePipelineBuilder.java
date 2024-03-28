package com.siglet.config.builder;

import java.util.List;

public class TracePipelineBuilder {

    private String name;

    private String start;

    private List<SpanletBuilder> spanletBuilders;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public List<SpanletBuilder> getSpanletBuilders() {
        return spanletBuilders;
    }

    public void setSpanletBuilders(List<SpanletBuilder> spanletBuilders) {
        this.spanletBuilders = spanletBuilders;
    }
}
