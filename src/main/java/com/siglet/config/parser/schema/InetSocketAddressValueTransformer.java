package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ValueTransformer;

import java.net.InetSocketAddress;

public class InetSocketAddressValueTransformer implements ValueTransformer {

    @Override
    public InetSocketAddress transform(Object value) {
        String[] parts = ((String) value).split(":");

        return InetSocketAddress.createUnresolved(parts[0], Integer.parseInt(parts[1]));
    }
}
