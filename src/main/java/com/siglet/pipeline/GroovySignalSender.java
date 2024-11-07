package com.siglet.pipeline;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

public interface GroovySignalSender {

    void send(String destination, Object protoAdapter);

    static GroovySignalSender create(Exchange exchange){
        return new SignalSender(exchange.getContext().createProducerTemplate());
//        return new SignalCreatorMock();
    }

    class SignalSender implements GroovySignalSender{

        private final ProducerTemplate producerTemplate;


        public SignalSender(ProducerTemplate producerTemplate) {
            this.producerTemplate = producerTemplate;
        }

        public void send(String destination, Object protoAdapter){
            producerTemplate.sendBody(destination, protoAdapter);
        }
    }

    class SignalCreatorMock implements GroovySignalSender{

        @Override
        public void send(String destination, Object protoAdapter) {
            System.out.println("sending to "+destination+" : "+protoAdapter);
        }

        public void test() {
            System.out.println("test");
        }
    }
}
