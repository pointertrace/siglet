package com.siglet.container.config.raw;

import com.siglet.api.parser.located.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayConfigTest {


    @Test
    void describe() {

        ArrayConfig<ValueConfig<String>> arrayConfig = new ArrayConfig<>(Location.of(1,1), List.of(
                new ValueConfig<>(Location.of(1,3),"first item"),
                new ValueConfig<>(Location.of(2,3),"second item"))
        );

        String expected = """
                (1:1)  arrayConfig
                  (1:3)  array item
                    (1:3)  String  (first item)
                  (2:3)  array item
                    (2:3)  String  (second item)""";

        assertEquals(expected, arrayConfig.describe());

    }

}