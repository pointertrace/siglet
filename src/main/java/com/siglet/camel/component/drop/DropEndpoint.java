package com.siglet.camel.component.drop;

import com.siglet.SigletError;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;

public class DropEndpoint extends DefaultEndpoint {

    public DropEndpoint(String uri, DropComponent component) {
        super(uri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new DropProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new SigletError("Drop cannot be used as message producer!");
    }
}
