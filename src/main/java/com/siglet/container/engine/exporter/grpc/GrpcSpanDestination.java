package com.siglet.container.engine.exporter.grpc;

import com.siglet.api.Signal;
import com.siglet.container.config.graph.SignalType;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.pipeline.accumulator.AccumulatedSpans;
import io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class GrpcSpanDestination implements SignalDestination {

    private static final Logger LOGGER = LogManager.getLogger(GrpcSpanDestination.class);

    private final TraceServiceGrpc.TraceServiceBlockingStub traceServiceStub;

    public GrpcSpanDestination(TraceServiceGrpc.TraceServiceBlockingStub traceServiceStub) {
        this.traceServiceStub = traceServiceStub;
    }


    @Override
    public String getName() {
        return "aggregated-spans";
    }

    @Override
    public boolean send(Signal signal) {
        try {
            ExportTraceServiceResponse response =
                    traceServiceStub.export(((AccumulatedSpans) signal).getRequest());

            if (response.hasPartialSuccess()) {
                ExportTracePartialSuccess partialSuccess = response.getPartialSuccess();

                if (partialSuccess.getRejectedSpans() > 0) {
                    LOGGER.error("Partial success sending spans with {} spans rejected. Error message: {}",
                            partialSuccess.getRejectedSpans(), partialSuccess.getErrorMessage());
                    return false;
                }
            } else {
                LOGGER.trace("Success sending spans");
            }
            return true;
        } catch (io.grpc.StatusRuntimeException e) {
            LOGGER.error("Error sending spans code:{} message:{}", e.getStatus().getCode(),
                    e.getStatus().getDescription());
            return false;
        }
    }

    @Override
    public Set<SignalType> getSignalCapabilities() {
        return Set.of(SignalType.SPAN);
    }
}
