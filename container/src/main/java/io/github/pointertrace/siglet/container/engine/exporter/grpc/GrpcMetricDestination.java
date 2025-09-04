package io.github.pointertrace.siglet.container.engine.exporter.grpc;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.engine.SignalDestination;
import io.github.pointertrace.siglet.container.engine.pipeline.accumulator.AccumulatedMetrics;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class GrpcMetricDestination implements SignalDestination {

    private static final Logger LOGGER = LogManager.getLogger(GrpcMetricDestination.class);

    private final MetricsServiceGrpc.MetricsServiceBlockingStub metricServiceStub;

    public GrpcMetricDestination(MetricsServiceGrpc.MetricsServiceBlockingStub metricServiceStub) {
        this.metricServiceStub = metricServiceStub;
    }


    @Override
    public String getName() {
        return "aggregated-spans";
    }

    @Override
    public boolean send(Signal signal) {
        try {
            ExportMetricsServiceResponse response = metricServiceStub.export(((AccumulatedMetrics) signal).getRequest());

            if (response.hasPartialSuccess()) {
                ExportMetricsPartialSuccess partialSuccess = response.getPartialSuccess();

                if (partialSuccess.getRejectedDataPoints() > 0) {
                    LOGGER.error("Partial success with {} metrics rejected metrics. Error message: {}",
                            partialSuccess.getRejectedDataPoints(), partialSuccess.getErrorMessage());
                    return false;
                }
            } else {
                LOGGER.trace("Success sending metrics");
            }
            return true;
        } catch (io.grpc.StatusRuntimeException e) {
            LOGGER.error("Error sending metrics code:{} message:{}", e.getStatus().getCode(),
                    e.getStatus().getDescription());
            return false;
        }
    }

    @Override
    public Set<SignalType> getSignalCapabilities() {
        return Set.of(SignalType.METRIC);
    }
}
