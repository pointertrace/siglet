package com.siglet.camel.component;

import com.siglet.data.adapter.ProtoSpanAdapter;
import io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

import java.io.IOException;

public class SigletProducer extends DefaultProducer {


    TraceServiceGrpc.TraceServiceBlockingStub traceServiceStub;


    public SigletProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ProtoSpanAdapter spanAdapter = exchange.getIn().getBody(ProtoSpanAdapter.class);
        Span span = spanAdapter.getUpdatedSpan();
        Resource resource = spanAdapter.getUpdatedResource();
        InstrumentationScope instrumentationScope = spanAdapter.getUpdatedInstrumentationScope();
        ExportTraceServiceRequest exportTraceServiceRequest = ExportTraceServiceRequest.newBuilder()
                .addResourceSpans(ResourceSpans.newBuilder()
                        .setResource(resource)
                        .addScopeSpans(ScopeSpans.newBuilder()
                                .setScope(instrumentationScope)
                                .addSpans(span))
                        .build())
                .build();

        ExportTraceServiceResponse resp = traceServiceStub.export(exportTraceServiceRequest);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        try {
            var builder = NettyChannelBuilder
                    .forAddress(((SigletEndpoint) getEndpoint()).getSocketAddress())
                    .usePlaintext();

            traceServiceStub = TraceServiceGrpc.newBlockingStub(builder.build());
        } catch (RuntimeException e) {
            System.out.println("error:" + e);
        }
    }
}
