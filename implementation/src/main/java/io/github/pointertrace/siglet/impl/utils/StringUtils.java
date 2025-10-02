package io.github.pointertrace.siglet.impl.utils;

public class StringUtils {

    private StringUtils() {
    }

    public static String frequency(long times) {
        return times == 2 ? "twice" : times + " times";
    }
}