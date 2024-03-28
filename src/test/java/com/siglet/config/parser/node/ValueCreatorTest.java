package com.siglet.config.parser.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueCreatorTest {
    
    
    @Test
    public void of() {
        
        ValueCreator valueCreator = ValueCreator.of(String::new);

        Object value = valueCreator.create();

        assertNotNull(value);
        assertEquals("", value);



        
    }
    

}