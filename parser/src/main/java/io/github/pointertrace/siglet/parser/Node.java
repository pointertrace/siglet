package io.github.pointertrace.siglet.parser;

import io.github.pointertrace.siglet.parser.located.Location;

public interface Node extends Describable {

    <T> T getValue(Class<T> clazz);

    Object getValue();

    Location getLocation();

    String describe(int ident);

}
