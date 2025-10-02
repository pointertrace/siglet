package io.github.pointertrace.siglet.impl.utils;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoiningTest {



    @Test
    void collection_oneElement() {
        Collection<String>  elements = List.of("one");

        assertEquals("one", Joining.collection(",", "and", elements));

    }


    @Test
    void collection_twoElement() {
        Collection<String>  elements = List.of("one","two");

        assertEquals("one and two", Joining.collection(",", " and ", elements));

    }

    @Test
    void collection_moreThanTwoElement() {
        Collection<String>  elements = List.of("one","two","three");

        assertEquals("one, two and three", Joining.collection(", ", " and ", elements));

    }



}