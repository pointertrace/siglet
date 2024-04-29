package com.siglet.spanlet.router;

import com.siglet.config.item.ArrayItem;
import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;

import java.lang.reflect.Array;
import java.util.List;

public class RouterConfig extends Item {

    private ArrayItem<Route> routes;

    private ValueItem<String> defaultRoute;


    public ArrayItem<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayItem<Route> routes) {
        this.routes = routes;
    }

    public void setDefaultRoute(ValueItem<String> defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    public ValueItem<String> getDefaultRoute() {
        return defaultRoute;
    }

}
