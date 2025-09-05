package io.github.pointertrace.siglet.parser.schema;


import io.github.pointertrace.siglet.parser.ValueTransformer;
import io.github.pointertrace.siglet.parser.ParserError;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class InetSocketAddressValueTransformer implements ValueTransformer {

    @Override
    public InetSocketAddress transform(Object value) {
        String[] parts = ((String) value).split(":");

        try {
            InetAddress host = InetAddress.getByName(parts[0]);
            return new InetSocketAddress(host, Integer.parseInt(parts[1]));
        } catch (UnknownHostException e) {
            throw new ParserError(String.format("Error creating address for %s. %s",value, e.getMessage()));
        }


    }
}
