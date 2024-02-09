package com.siglet.camel.component;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.PropertyConfigurer;
import org.apache.camel.support.DefaultComponent;
import org.checkerframework.checker.units.qual.N;

import java.io.IOException;
import java.util.Map;

public class SigletComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {


        return new SigletEndpoint(uri, this);
    }

    @Override
    public PropertyConfigurer getEndpointPropertyConfigurer() {

//        return new MyProertyConfigurer();
        return super.getEndpointPropertyConfigurer();
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    public static class MyProertyConfigurer implements PropertyConfigurer {

        @Override
        public boolean configure(CamelContext camelContext, Object target, String name, Object value, boolean ignoreCase) {
//            SigletEndpoint ep = (SigletEndpoint) target;
//            if (name.equals("a")) {
//                if (value instanceof String a) {
//                    return true;
//                }
//            }
            return false;
        }
    }
}
