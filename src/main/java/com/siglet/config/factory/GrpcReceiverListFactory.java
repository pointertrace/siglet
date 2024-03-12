package com.siglet.config.factory;

import com.siglet.config.GrpcReceiver;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GrpcReceiverListFactory {

    private GrpcReceiverFactory grpcReceiverFactory = new GrpcReceiverFactory();

    public List<GrpcReceiver> create(List<Map<String,Object>> config) {
        List<GrpcReceiver> result = new ArrayList<>();
        for(Map<String,Object> item: config) {
            result.add(grpcReceiverFactory.create(item));
        }

        return result;
    }

    public boolean check(List<Map<String,Object>> config) {
        for(Map<String,Object> item: config){
            if (! grpcReceiverFactory.check(item)) {
                return false;
            }
        }
        return true;
    }
}
