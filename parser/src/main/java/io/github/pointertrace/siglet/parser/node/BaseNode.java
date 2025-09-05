package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.Utils;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;


public abstract class BaseNode implements Node, Located {

    private static final String PREFIX = "  ";

    private Location location;

    private ValueSetter valueSetter;

    private ValueCreator valueCreator;

    private LocationSetter locationSetter = LocationSetter.EMPTY;

    protected BaseNode(Location location) {
        this.location = location;
    }


    public <T> T getValue(Class<T> clazz) {
        Object value = getValue();

        if (! clazz.isInstance(value)) {
            throw new SigletParserError(String.format("Expecting value to be instance of %s but it is %s",
                    clazz.getName(),value.getClass().getName()),getLocation());
        }
        return clazz.cast(value);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
    }

    public void setValueSetter(ValueSetter valueSetter) {
        this.valueSetter = valueSetter;
    }

    public void setLocationSetter(LocationSetter locationSetter) {
        this.locationSetter = locationSetter;
    }

    public LocationSetter getLocationSetter() {
        return locationSetter;
    }

    public String describe() {
        return describe(0);
    }

    public abstract String describe(int level);

    protected String getDescriptionPrefix(int level) {
        return Utils.repeat(PREFIX,level);
    }

    public ValueCreator getValueCreator() {
        return valueCreator;
    }

    public void setValueCreator(ValueCreator valueCreator) {
        this.valueCreator = valueCreator;
    }
}
