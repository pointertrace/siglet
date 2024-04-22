package com.siglet.config.item.repository.routecreator;

import java.util.concurrent.atomic.AtomicInteger;

public class RouteLink {

    private final AtomicInteger seed;

    private final String prefix;

    public RouteLink(AtomicInteger seed, String prefix) {
        this.seed = seed;
        this.prefix = prefix;
    }

    public void increment() {
        seed.getAndIncrement();
    }

    public String getLink() {
        return "direct:" + prefix + "_" + seed.get();
    }
}
