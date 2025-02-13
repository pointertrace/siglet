package com.siglet.pipeline.processor.common.action.groovy;

import com.siglet.SigletError;
import com.siglet.cli.SigletContext;
import org.apache.camel.CamelContext;

public class SignalSender {

    private final String destinationUri;

    private final CamelContext context;

    public SignalSender(String destination) {
        this.destinationUri = SigletContext.getInstance().getReceiverConsumers().get(destination);
        if (destinationUri == null) {
            throw new SigletError("Could not find receiver [" + destination + "]");
        }
        this.context = SigletContext.getInstance().getContextSupplier().get();
    }

    public void send(Object message) {
        context.createProducerTemplate().sendBody(destinationUri, message);
    }

    public void send(Object[] messages) {
        for (Object message : messages) {
            context.createProducerTemplate().sendBody(destinationUri, message);
        }
    }
}
