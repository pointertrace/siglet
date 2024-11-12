package com.siglet.config;

import com.siglet.config.item.ConfigItem;
import com.siglet.config.item.ReceiverItem;
import org.apache.camel.builder.RouteBuilder;

import java.util.Map;
import java.util.stream.Collectors;

public class Config {

    private final RouteBuilder routeBuilder;

    private final Map<String, String> receiversUris;

    public Config(ConfigItem configItem) {
        this.routeBuilder = configItem.build();
        this.receiversUris = configItem.getReceivers().stream().collect(Collectors.toMap(
                (ri) -> ri.getName().getValue(), ReceiverItem::getUri));
    }

    public RouteBuilder getRouteBuilder() {
        return routeBuilder;
    }

    public Map<String, String> getReceiversUris() {
        return receiversUris;
    }

}
