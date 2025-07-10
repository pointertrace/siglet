package com.siglet.container.config.siglet.parser;

import com.siglet.parser.ValueTransformer;
import com.siglet.parser.ValueTransformerException;

public class ClassValueTransformer implements ValueTransformer {


    private Class<?> instanceOf;

    private ClassLoader classLoader;

    public ClassValueTransformer() {
    }

    public ClassValueTransformer(Class<?> instanceOf, ClassLoader classLoader) {
        this.instanceOf = instanceOf;
        this.classLoader = classLoader;
    }

    public ClassValueTransformer(Class<?> instanceOf) {
        this.instanceOf = instanceOf;
    }

    @Override
    public Object transform(Object value) throws ValueTransformerException {
        if (value == null) {
            throw new ValueTransformerException("The value is null");
        }
        if (!(value instanceof String strValue)) {
            throw new ValueTransformerException("The value is " + value.getClass().getName() +
                    " and should be a String");
        }
        try {
            Class<?> clazz = null;
            if (classLoader != null) {
                clazz = classLoader.loadClass(strValue);
            } else {
                clazz = Class.forName(strValue);
            }
            if (instanceOf != null && !instanceOf.isAssignableFrom(clazz)) {
                throw new ValueTransformerException(String.format("Class %s is not assignable from %s.",
                        clazz.getName(), instanceOf.getName()));
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new ValueTransformerException(String.format("Class %s not found", strValue), e);
        }

    }
}
