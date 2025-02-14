package com.siglet.config.item;

public interface Describable {

    String describe(int level);

    default String prefix(int level) {
        return "  ".repeat(level);
    }
}
