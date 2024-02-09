package com.siglet.camel.component;

import io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
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
//        SpanAdapter spanAdapter = exchange.getIn().getBody(SpanAdapter.class);
//        ExportTraceServiceRequest exportTraceServiceRequest = ExportTraceServiceRequest.newBuilder()
//                .addResourceSpans(ResourceSpans.newBuilder()
//                        .setResource(spanAdapter.getProtoResource())
//                        .addScopeSpans(ScopeSpans.newBuilder()
//                                .setScope(spanAdapter.getProtoInstrumentationScope())
//                                .addSpans(spanAdapter.getProtoSpan()))
//                        .build())
//                .build();
//
//        ExportTraceServiceResponse resp = traceServiceStub.export(exportTraceServiceRequest);
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
