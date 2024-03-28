package com.siglet.config.parser.node;

import java.util.function.Function;

public interface ValueTransformer {


    Object transform(Object value);


    static <T,E> ValueTransformer of(Function<T,E> transformer) {
        return (Object value ) -> transformer.apply ((T) value) ;

    }



}
