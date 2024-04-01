package com.siglet.config.builder;

import java.util.List;

public class TracePipelineBuilder {

    private String name;

    private List<SpanletBuilder> spanletBuilders;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SpanletBuilder> getSpanletBuilders() {
        return spanletBuilders;
    }

    public void setSpanletBuilders(List<SpanletBuilder> spanletBuilders) {
        this.spanletBuilders = spanletBuilders;
    }
}
