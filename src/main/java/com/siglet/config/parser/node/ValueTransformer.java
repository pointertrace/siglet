package com.siglet.config.parser.node;

import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;

import java.util.function.BiFunction;

public interface ValueTransformer {


    ValueItem<?> transform(Location location, Object value);


    static <T extends Location,U extends Object, R extends ValueItem> ValueTransformer of(BiFunction<T,U, R> transformer) {
        return (Location location, Object value ) -> transformer.apply ((T) location, (U) value) ;

    }



}
