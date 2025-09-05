package io.github.pointertrace.siglet.api.signal.metric;

public interface Gauge extends Data {

   NumberDataPoints getDataPoints();

}
