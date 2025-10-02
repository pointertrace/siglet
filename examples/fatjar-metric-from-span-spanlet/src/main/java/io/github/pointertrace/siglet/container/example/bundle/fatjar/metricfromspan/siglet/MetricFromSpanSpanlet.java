package io.github.pointertrace.siglet.container.example.bundle.fatjar.metricfromspan.siglet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;



/**
 * Implementation of the {@link Spanlet} interface for processing a {@link Span}
 * and deriving a metric from its properties.
 *
 * This class specifically calculates the duration of a span in nanoseconds and
 * generates a corresponding metric. The metric is named "span_duration_fatjar",
 * with a description "span duration from fatjar", and the unit "nanoseconds".
 * The duration is calculated as the difference between the Unix end time and
 * the Unix start time of the span.
 *
 * The generated metric contains the following properties:
 * - The duration of the span in nanoseconds.
 * - The Unix start time of the span as the timestamp for the metric data point.
 * - The name of the span as an attribute in the generated metrics.
 *
 * After processing the span, the metric is sent to a destination named
 * "metric-destination".
 *
 * This implementation relies on the {@link Context} to create the metric, the
 * {@link Span} for obtaining span details, and the {@link ResultFactory} for
 * producing the final result.
 *
 * The siglet configura
 * @see Spanlet
 * @see Span
 * @see Metric
 * @see Context
 * @see ResultFactory
 */
public class MetricFromSpanSpanlet implements Spanlet<Void> {


    @Override
    public Result span(Span span, Context<Void> context, ResultFactory resultFactory) {

        Metric metric = context.newGauge(span);

        metric.setName("span_duration_fatjar")
                .setDescription("span duration from fatjar")
                .setUnit("nanoseconds").getGauge()
                .getDataPoints().add()
                .setAsLong(span.getEndTimeUnixNano() - span.getStartTimeUnixNano())
                .setTimeUnixNano(span.getStartTimeUnixNano())
                .getAttributes().set("span-name", span.getName());

        return resultFactory.proceed().andSend(metric, "metric-destination");
    }

}
