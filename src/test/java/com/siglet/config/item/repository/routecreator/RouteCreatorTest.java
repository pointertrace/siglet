package com.siglet.config.item.repository.routecreator;

import com.google.protobuf.ByteString;
import com.siglet.data.CloneableAdapter;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.pipeline.common.processor.GroovyProcessor;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteCreatorTest extends CamelTestSupport {
    @Test
    public void testSimple() throws Exception {
        /*
        Test for a simple route

        receiver(start) -> processor(sum(1)) -> processor(sum(3)) -> exporter(output)
         */


        RootRouteCreator root = new RootRouteCreator();

        root
                .addReceiver("direct:start", "start")
                .addProcessor(new SumProcessor(1))
                .addProcessor(new SumProcessor(3))
                .addExporter("mock:output");

        context.addRoutes(root.getRouteBuilder());


        CloneableAdapterInteger i = new CloneableAdapterInteger(1);
        template.sendBody("direct:start", i);


        MockEndpoint mock = getMockEndpoint("mock:output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        assertEquals(5, mock.getExchanges().getFirst().getIn().getBody(CloneableAdapterInteger.class).getValue());

    }

    /*

    receiver(1,start);
      -> processor(1_1,sum(1))
        -> aggregator(1_1_1)
        -> exporter(second-exporter)
      -> exporter(first-exporter)
    */
    @Test
    public void processorToMulticast() throws Exception {

        var firstProtoSpan = new ProtoSpanAdapter(Span.newBuilder()
                .setName("first-span")
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 1)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .build(),
                Resource.newBuilder().build(),
                InstrumentationScope.newBuilder().build(), true);

        var secondProtoSpan = new ProtoSpanAdapter(Span.newBuilder()
                .setName("second-span")
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 1)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .build(),
                Resource.newBuilder().build(),
                InstrumentationScope.newBuilder().build(), true);

        RootRouteCreator root = new RootRouteCreator();
        RouteCreator receiver = root
                .addReceiver("direct:start", "start");

        var processor1_1 = receiver.addProcessor(new GroovyProcessor("thisSignal.name = thisSignal.name + \"_altered\""));
        var aggregator = processor1_1.traceAggregator("thisSignal.size == 2", 1_000L,1_000L);
        aggregator.addExporter("mock:first-exporter");
        var multicast = receiver.startMulticast();
        multicast.addExporter("mock:second-exporter");
        multicast.endMulticast();

        context.addRoutes(root.getRouteBuilder());



        template.sendBody("direct:start", firstProtoSpan );


        MockEndpoint firstExporter = getMockEndpoint("mock:first-exporter");

        MockEndpoint secondExporter = getMockEndpoint("mock:second-exporter");

        secondExporter.expectedMessageCount(1);

        assertEquals(1, secondExporter.getExchanges().size());
        var firstSpanAdapter = secondExporter.getExchanges().getFirst().getIn().getBody(ProtoSpanAdapter.class);
        assertEquals(firstProtoSpan.getTraceIdHigh(), firstSpanAdapter.getTraceIdHigh());
        assertEquals(firstProtoSpan.getTraceIdLow(), firstSpanAdapter.getTraceIdLow());
        assertEquals(firstProtoSpan.getName(), firstSpanAdapter.getName());

    }

    @Test
    public void processorToMulticastX() throws Exception {

        var firstProtoSpan = new ProtoSpanAdapter(Span.newBuilder()
                .setName("first-span")
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 1)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .build(),
                Resource.newBuilder().build(),
                InstrumentationScope.newBuilder().build(), true);

        var secondProtoSpan = new ProtoSpanAdapter(Span.newBuilder()
                .setName("second-span")
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 1)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(2)))
                .build(),
                Resource.newBuilder().build(),
                InstrumentationScope.newBuilder().build(), true);

        RootRouteCreator root = new RootRouteCreator();
        RouteCreator receiver = root
                .addReceiver("direct:start", "start");

        var multicast = receiver.startMulticast();
        var processor1_1 = multicast.addProcessor(new GroovyProcessor("thisSignal.name = thisSignal.name + \"_altered\""));
        var aggregator = processor1_1.traceAggregator("thisSignal.size == 2", 1_000L,1_000L);
        aggregator.addExporter("mock:first-exporter");
        multicast.addExporter("mock:second-exporter");
        multicast.endMulticast();

        context.addRoutes(root.getRouteBuilder());



        template.sendBody("direct:start", firstProtoSpan );


        MockEndpoint firstExporter = getMockEndpoint("mock:first-exporter");

        MockEndpoint secondExporter = getMockEndpoint("mock:second-exporter");

        secondExporter.expectedMessageCount(1);

        assertEquals(1, secondExporter.getExchanges().size());
        var firstSpanAdapter = secondExporter.getExchanges().getFirst().getIn().getBody(ProtoSpanAdapter.class);
        assertEquals(firstProtoSpan.getTraceIdHigh(), firstSpanAdapter.getTraceIdHigh());
        assertEquals(firstProtoSpan.getTraceIdLow(), firstSpanAdapter.getTraceIdLow());
        assertEquals(firstProtoSpan.getName(), firstSpanAdapter.getName());

    }
    // TODO new test!
//    @Test
    public void complex() throws Exception {
        /*
        Test for a complex route

        receiver(1,first-start);
          -> processor(1_1,sum(1))
             -> processor(1_1_1,sum(10)) -> exporter(first-output)
             -> processor(1_1_2,sum(20)) -> exporter(second-output)
          -> processor(1_2,sum(100)
             -> processor((1_2_1,sum(1000)) -> exporter(third-output)
             -> processor(1_2_2,sum(2000)) -> exporter(forth-output)
          -> exporter(fifth-output)
        receiver(2,second-start)
          -> processor(2,sum(10000))
            -> exporter(sixth-output)
         */


        RootRouteCreator root = new RootRouteCreator();

        RouteCreator receiver1 = root
                .addReceiver("direct:first-start", "start");

        var receiver1Multicast = receiver1.startMulticast();

        var processor1_1 = receiver1Multicast.addProcessor(new SumProcessor(1));

        var processor1_1_multicast = processor1_1.startMulticast();

        processor1_1_multicast.addProcessor(new SumProcessor(10)).addExporter("mock:first-output");
        processor1_1_multicast.addProcessor(new SumProcessor(20)).addExporter("mock:second-output");

        processor1_1_multicast.endMulticast();

        var processor1_2 = receiver1Multicast.addProcessor(new SumProcessor(100));

        var processor_1_2_multicast = processor1_2.startMulticast();

        processor_1_2_multicast.addProcessor(new SumProcessor(1000)).addExporter("mock:third-output");
        processor_1_2_multicast.addProcessor(new SumProcessor(2000)).addExporter("mock:forth-output");

        processor_1_2_multicast.endMulticast();

        receiver1Multicast.addExporter("mock:fifth-output");

        receiver1Multicast.endMulticast();

        RouteCreator receiver2 = root.addReceiver("direct:second-start", "second-start");

        receiver2.addProcessor(new SumProcessor(10000))
                .addExporter("mock:sixth-output");


        context.addRoutes(root.getRouteBuilder());


        template.sendBody("direct:first-start", new CloneableAdapterInteger(0));
        template.sendBody("direct:second-start", new CloneableAdapterInteger(0));


        MockEndpoint mock = getMockEndpoint("mock:first-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(11, mock.getExchanges().getFirst().getIn().getBody(CloneableAdapterInteger.class).getValue());

        mock = getMockEndpoint("mock:second-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(21, mock.getExchanges().getFirst().getIn().getBody(CloneableAdapterInteger.class).getValue());

        mock = getMockEndpoint("mock:third-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(1100, mock.getExchanges().getFirst().getIn().getBody(CloneableAdapterInteger.class).getValue());

        mock = getMockEndpoint("mock:forth-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(2100, mock.getExchanges().getFirst().getIn().getBody(CloneableAdapterInteger.class).getValue());

        mock = getMockEndpoint("mock:fifth-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(0, mock.getExchanges().getFirst().getIn().getBody(CloneableAdapterInteger.class).getValue());

        mock = getMockEndpoint("mock:sixth-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(10000, mock.getExchanges().getFirst().getIn().getBody(CloneableAdapterInteger.class).getValue());
    }

    public static class SumProcessor implements Processor {

        private final Integer summand;

        public SumProcessor(Integer summand) {
            this.summand = summand;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            CloneableAdapterInteger i = exchange.getIn().getBody(CloneableAdapterInteger.class);
            exchange.getIn().setBody(new CloneableAdapterInteger(i.getValue() + summand));
        }
    }

    public static class CloneableAdapterInteger implements CloneableAdapter<CloneableAdapterInteger> {

        private final int value;

        public CloneableAdapterInteger(int value) {
            this.value = value;
        }

        @Override
        public Object clone() {
            return new CloneableAdapterInteger(value);
        }

        public int getValue() {
            return value;
        }

        @Override
        public CloneableAdapterInteger cloneAdapter() {
            return null;
        }
    }

}