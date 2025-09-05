package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.located.Location;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface LocationSetter {


    LocationSetter EMPTY = (Object located, Location location) -> {
    };

    void setLocation(Object located, Location location);


        static <T> LocationSetter of(BiConsumer<T, Location> locationSetter) {
        return (Object located, Location location) -> locationSetter.accept((T) located, location);
    }



}
