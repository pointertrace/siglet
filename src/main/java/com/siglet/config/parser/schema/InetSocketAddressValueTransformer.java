package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;
import com.siglet.config.parser.node.ValueTransformer;

import java.net.InetSocketAddress;

public class InetSocketAddressValueTransformer implements ValueTransformer {

    @Override
    public ValueItem<InetSocketAddress> transform(Location location, Object value) {
        String[] parts = ((String) value).split(":");

        return new ValueItem<>(location, InetSocketAddress.createUnresolved(parts[0], Integer.parseInt(parts[1])));
    }
}
