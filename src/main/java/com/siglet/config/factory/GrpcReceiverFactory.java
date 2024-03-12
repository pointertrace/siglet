package com.siglet.config.factory;

import com.siglet.config.GrpcReceiver;
import com.siglet.config.Receiver;

import java.net.InetSocketAddress;
import java.util.Map;

public class GrpcReceiverFactory {

    public GrpcReceiver create(Map<String, Object> config) {
        String name = (String) config.get("name");
        String[] addrParts = ((String) config.get("address")).split(":");

        return new GrpcReceiver(name, InetSocketAddress.createUnresolved(addrParts[0], Integer.parseInt(addrParts[1])));
    }

    public boolean check(Map<String,Object> config) {
        return FactoryUtils.checkProperty(config,"name", String.class) &&
        FactoryUtils.checkProperty(config,"address",String.class, FactoryUtils::isAddress);
    }
}
