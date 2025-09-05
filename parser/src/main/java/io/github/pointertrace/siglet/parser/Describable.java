package io.github.pointertrace.siglet.parser;

public interface Describable {

    String PREFIX_VALUE = "  ";

    String describe(int level);

    static String prefix(int level) {
        return Utils.repeat(PREFIX_VALUE,level);
    }

}
