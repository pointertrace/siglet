package io.github.pointertrace.siglet.parser;

import java.util.function.Function;

public interface ValueTransformer {

    Object transform( Object value) throws ValueTransformerException;

    static <T, U> ValueTransformer of(Function<T,U> transformer) {
        return (Object value ) -> transformer.apply ((T) value) ;

    }}
