package com.siglet.config.item.repository.routecreator;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteLinkTest {

    @Test
    void incrementAndGetString() {

        AtomicInteger seed = new AtomicInteger(1);

        RouteLink rootRouteLink = new RouteLink(seed, "root-prefix");
        RouteLink childRouteLink = new RouteLink(seed, "child-prefix");

        assertEquals("direct:root-prefix_1" , rootRouteLink.getLink());
        assertEquals("direct:child-prefix_1" , childRouteLink.getLink());

        rootRouteLink.increment();

        assertEquals("direct:root-prefix_2" , rootRouteLink.getLink());
        assertEquals("direct:child-prefix_2" , childRouteLink.getLink());
    }

}