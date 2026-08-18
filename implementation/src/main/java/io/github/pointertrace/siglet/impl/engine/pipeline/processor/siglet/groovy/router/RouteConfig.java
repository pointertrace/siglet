package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.router;


import io.github.pointertrace.siglet.parser.StringValue;

public class RouteConfig {

    private StringValue when;

    private StringValue to;

    public StringValue getWhen() {
        return when;
    }

    public void setWhen(StringValue when) {
        this.when = when;
    }

    public StringValue getTo() {
        return to;
    }

    public void setTo(StringValue to) {
        this.to = to;
    }

}
