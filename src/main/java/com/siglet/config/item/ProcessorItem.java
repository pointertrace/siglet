package com.siglet.config.item;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ProcessorItem extends Item {

    private Item config;

    private ValueItem<String> pipeline;

   private ArrayItem<ValueItem<String>> to;


    public ValueItem<String> getPipeline() {
        return pipeline;
    }

    public void setPipeline(ValueItem<String> pipeline) {
        this.pipeline = pipeline;
    }

    public ArrayItem<ValueItem<String>> getTo() {
        return to;
    }

    public void setTo(ArrayItem<ValueItem<String>> to) {
        this.to = to;
    }

    public void setToSingleValue(ValueItem<String> to)  {
        this.to = new ArrayItem<>(to.getLocation(),List.of(to));
    }

    public Item getConfig() {
        return config;
    }

    public void setConfig(Item config) {
        this.config = config;
    }
}
