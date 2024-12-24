package com.siglet.config.item;

import com.siglet.config.located.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayItemTest {


    @Test
    void describe() {

        ArrayItem<ValueItem<String>> arrayItem = new ArrayItem<>(Location.of(1,1), List.of(
                new ValueItem<>(Location.of(1,3),"first item"),
                new ValueItem<>(Location.of(2,3),"second item"))
        );

        String expected = """
                (1:1)  arrayItem
                  (1:3)  array item
                    (1:3)  String  (first item)
                  (2:3)  array item
                    (2:3)  String  (second item)""";

        assertEquals(expected, arrayItem.describe());

    }

}