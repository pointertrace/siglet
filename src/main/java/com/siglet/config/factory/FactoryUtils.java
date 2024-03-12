package com.siglet.config.factory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.function.Function;

public class FactoryUtils {

    public static boolean isAddress(String value) {
        String[] parts = value.split(":");
        if (parts.length != 2) {
            return false;
        }
        try {
            InetSocketAddress.createUnresolved(parts[0], Integer.parseInt(parts[1]));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public static <T> boolean checkProperty(Map<String, Object> config, String key, Class<T> type, Function<T, Boolean>... checkFunctions) {
        if (config.containsKey(key)) {
            Object value = config.get(key);
            if (type.isAssignableFrom(value.getClass())) {
                for (Function<T, Boolean> checkFunction : checkFunctions) {
                    if (!checkFunction.apply((T) value)) {
                        return false;
                    }
                    return true;
                }

            }
        }
        return false;
    }


}
