package com.siglet.config.parser.node;

import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueCreatorTest {
    
    
    @Test
    public void of() {
        
        ValueCreator valueCreator = ValueCreator.of(StringItem::new);

        Object value = valueCreator.create();

        assertNotNull(value);
        assertInstanceOf(StringItem.class, value);

    }

    public static class StringItem extends Item {

    }
    

}