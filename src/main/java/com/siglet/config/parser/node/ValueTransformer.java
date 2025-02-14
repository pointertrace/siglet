package com.siglet.config.parser.node;

import java.util.function.Function;

public interface ValueTransformer {


    Object transform( Object value);


    static <T, U> ValueTransformer of(Function<T,U> transformer) {
        return (Object value ) -> transformer.apply ((T) value) ;

    }



}
