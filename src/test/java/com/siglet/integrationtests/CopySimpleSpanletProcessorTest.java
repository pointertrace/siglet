package com.siglet.integrationtests;

import com.google.protobuf.ByteString;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.ProtoSpanAdapter;
import com.siglet.data.adapter.ProtoTraceAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class CopySimpleSpanletProcessorTest extends CamelTestSupport {


    @Test
    public void test() throws Exception {


        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .aggregate(new Expression() {
                            @Override
                            public <T> T evaluate(Exchange exchange, Class<T> type) {
                                ProtoSpanAdapter span = exchange.getIn().getBody(ProtoSpanAdapter.class);
                                String str = new String(Base64.getEncoder().encode(span.getTraceId()));
                                return type.cast(str);
                            }
                        }, new TraceAggregationStrategy())
                        .completionSize(2)
                        .to("mock:output");
            }
        });


        Span firstSpan =  Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 2)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setName("span-name")
        .build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter1 = new ProtoSpanAdapter(firstSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter1);

        Span secondSpan =  Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 2)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setParentSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .setName("span-name")
                .build();
        ProtoSpanAdapter protoSpanAdapter2 = new ProtoSpanAdapter(secondSpan, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter2);



        MockEndpoint mock = getMockEndpoint("mock:output");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();



        assertEquals(1, mock.getExchanges().size());
        var traceAdapter = assertInstanceOf(ProtoTraceAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals(2, traceAdapter.size());
        assertEquals(0, traceAdapter.getTraceIdHigh());
        assertEquals(2, traceAdapter.getTraceIdLow());
        assertNotNull(traceAdapter.get(1));
        assertNotNull(traceAdapter.get(2));

    }


    public static class TraceAggregationStrategy implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                ProtoTraceAdapter protoTraceAdapter =
                        new ProtoTraceAdapter(newExchange.getIn().getBody(ProtoSpanAdapter.class), true);
                newExchange.getIn().setBody(protoTraceAdapter);
                return newExchange;
            } else {
                ProtoTraceAdapter protoTraceAdapter = oldExchange.getIn().getBody(ProtoTraceAdapter.class);
                protoTraceAdapter.add(newExchange.getIn().getBody(ProtoSpanAdapter.class));
                return oldExchange;
            }
        }
    }

}
