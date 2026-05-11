package io.github.pointertrace.siglet.container.example.bundle.springboot.metricfromspan.siglet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.metric.NumberDataPoint;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import org.springframework.stereotype.Component;

@Component
public class MetricFromSpanSpanlet implements Spanlet<MetricFromSpanConfig> {

    @Override
    public Result span(Span span, Context<MetricFromSpanConfig> context, ResultFactory resultFactory) {

        Metric metric = context.newGauge(span);

        metric.setName("span_duration_springboot")
                .setDescription("span duration from springboot")
                .setUnit("nanoseconds")
                .getGauge()
                .getDataPoints().add()
                .setAsLong(span.getEndTimeUnixNano() - span.getStartTimeUnixNano())
                .setTimeUnixNano(span.getStartTimeUnixNano())
                .getAttributes().set("span-name", span.getName()).set("extra",context.getConfig().getExtra());

        return resultFactory.proceed().andSend(metric, "metric-destination");
    }

}
