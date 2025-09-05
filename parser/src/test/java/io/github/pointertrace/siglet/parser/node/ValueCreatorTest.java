package io.github.pointertrace.siglet.parser.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValueCreatorTest {
    
    
    @Test
    void of() {
        
        ValueCreator valueCreator = ValueCreator.of(StringItem::new);

        Object value = valueCreator.create();

        assertNotNull(value);
        assertInstanceOf(StringItem.class, value);

    }

    public static class StringItem {

    }
    

}