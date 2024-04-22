package com.siglet.config.item.repository.routecreator;

import com.siglet.data.Clonable;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteCreatorTest extends CamelTestSupport {
    @Test
    public void testSimple() throws Exception {
        /*
        Test for a simple route

        receiver(start) -> processor(sum(1)) -> processor(sum(3)) -> exporter(output)
         */


        RootRouteCreator root = new RootRouteCreator();

        root
                .addReceiver("direct:start")
                .addProcessor(new SumProcessor(1))
                .addProcessor(new SumProcessor(3))
                .addExporter("mock:output");

        context.addRoutes(root.getRouteBuilder());


        ClonableInteger i = new ClonableInteger(1);
        template.sendBody("direct:start", i);


        MockEndpoint mock = getMockEndpoint("mock:output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        assertEquals(5, mock.getExchanges().getFirst().getIn().getBody(ClonableInteger.class).getValue());

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
                .addReceiver("direct:first-start");

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

        RouteCreator receiver2 = root.addReceiver("direct:second-start");

        receiver2.addProcessor(new SumProcessor(10000))
                .addExporter("mock:sixth-output");


        context.addRoutes(root.getRouteBuilder());


        template.sendBody("direct:first-start", new ClonableInteger(0));
        template.sendBody("direct:second-start", new ClonableInteger(0));


        MockEndpoint mock = getMockEndpoint("mock:first-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(11, mock.getExchanges().getFirst().getIn().getBody(ClonableInteger.class).getValue());

        mock = getMockEndpoint("mock:second-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(21, mock.getExchanges().getFirst().getIn().getBody(ClonableInteger.class).getValue());

        mock = getMockEndpoint("mock:third-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(1100, mock.getExchanges().getFirst().getIn().getBody(ClonableInteger.class).getValue());

        mock = getMockEndpoint("mock:forth-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(2100, mock.getExchanges().getFirst().getIn().getBody(ClonableInteger.class).getValue());

        mock = getMockEndpoint("mock:fifth-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(0, mock.getExchanges().getFirst().getIn().getBody(ClonableInteger.class).getValue());

        mock = getMockEndpoint("mock:sixth-output");

        mock.expectedMessageCount(1);

        assertEquals(1, mock.getExchanges().size());
        assertEquals(10000, mock.getExchanges().getFirst().getIn().getBody(ClonableInteger.class).getValue());
    }

    public static class SumProcessor implements Processor {

        private final Integer summand;

        public SumProcessor(Integer summand) {
            this.summand = summand;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            ClonableInteger i = exchange.getIn().getBody(ClonableInteger.class);
            exchange.getIn().setBody(new ClonableInteger(i.getValue() + summand));
        }
    }

    public static class ClonableInteger implements Clonable {

        private final int value;

        public ClonableInteger(int value) {
            this.value = value;
        }

        @Override
        public Object clone() {
            return new ClonableInteger(value);
        }

        public int getValue() {
            return value;
        }
    }

}