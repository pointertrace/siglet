package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.ParserError;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

import java.util.ArrayList;
import java.util.List;

public final class ArrayNode extends BaseNode {

    private final List<BaseNode> items;

    private ValueCreator arraycontainerValueCreator;

    private ValueSetter arrayContainerValueSetter;

    private ValueCreator arrayItemCreator;

    private ValueSetter arrayItemValueSetter;

    ArrayNode(List<BaseNode> items, Location location) {
        super(location);
        this.items = new ArrayList<>(items);
    }

    public int getLength() {
        return items.size();
    }

    public BaseNode getItem(int i) {
        return items.get(i);
    }

    @Override
    public Object getValue() {

        Object container = null;
        ValueSetter valueSetter = null;

        if (getArrayContainerValueCreator()!= null) {
            container = getArrayContainerValueCreator().create();
        } else {
            container = new ArrayList<>();
        }

        if (getArrayContainerValueSetter() != null) {
            valueSetter = getArrayContainerValueSetter();
        } else {
            valueSetter = ValueSetter.listAdd();
        }
        Located.set(container, getLocation());
        for (int i = 0; i < getLength(); i++) {
            BaseNode currentItem = items.get(i);
            Object currentItemValue = currentItem.getValue();

            if (currentItemValue instanceof Located) {
                Located locatedItemValue = (Located) currentItemValue;
                LocationSetter locationSetter = items.get(i).getLocationSetter();
                locationSetter.setLocation(locatedItemValue, items.get(i).getLocation());
            }
            if (getArrayItemCreator() != null) {
                Object arrayItem = getArrayItemCreator().create();
                Located.set(arrayItem, items.get(i).getLocation());
                if (getArrayItemValueSetter() == null) {
                    throw new ParserError("ArrayItemValueSetter must be informed if ArrayItemCreator is informed!");
                }
                getArrayItemValueSetter().set(arrayItem, currentItemValue);
                valueSetter.set(container, arrayItem);
            } else {
                valueSetter.set(container, currentItemValue);
            }
        }
        return container;
    }


    public ValueCreator getArrayItemCreator() {
        return arrayItemCreator;
    }

    public void setArrayItemCreator(ValueCreator arrayItemCreator) {
        this.arrayItemCreator = arrayItemCreator;
    }

    public ValueSetter getArrayItemValueSetter() {
        return arrayItemValueSetter;
    }

    public void setArrayItemValueSetter(ValueSetter arrayItemValueSetter) {
        this.arrayItemValueSetter = arrayItemValueSetter;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  array");
        for (BaseNode child : items) {
            sb.append("\n");
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(child.getLocation().describe());
            sb.append("  array item");
            sb.append("\n");
            sb.append(child.describe(level + 2));
        }
        return sb.toString();
    }

    public ValueCreator getArrayContainerValueCreator() {
        return arraycontainerValueCreator;
    }

    public void setArraycontainerValueCreator(ValueCreator arraycontainerValueCreator) {
        this.arraycontainerValueCreator = arraycontainerValueCreator;
    }

    public ValueSetter getArrayContainerValueSetter() {
        return arrayContainerValueSetter;
    }

    public void setArrayContainerValueSetter(ValueSetter arrayContainerValueSetter) {
        this.arrayContainerValueSetter = arrayContainerValueSetter;
    }
}
