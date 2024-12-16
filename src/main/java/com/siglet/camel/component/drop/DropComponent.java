package com.siglet.camel.component.drop;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

public class DropComponent extends DefaultComponent {


    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new DropEndpoint(uri, this);
    }

}
