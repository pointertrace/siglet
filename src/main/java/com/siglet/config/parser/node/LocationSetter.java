package com.siglet.config.parser.node;

import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface LocationSetter {


    LocationSetter EMPTY = (Located located, Location location) -> {
    };

    void setLocation(Located located, Location location);

    static <T extends Located> LocationSetter of(BiConsumer<T, Location> locationSetter) {
        return (Located located, Location location) -> locationSetter.accept((T) located, location);
    }
}
