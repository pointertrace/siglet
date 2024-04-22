package com.siglet.config.parser.schema;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LongCheckerTest {


    @Test
    public void getValue() throws Exception {

        LongChecker longChecker = new LongChecker();


        ConfigParser parser = new ConfigParser();

        ConfigNode longConfigNode = parser.parse("" + ((long) Integer.MAX_VALUE)+  1);

        longChecker.check(longConfigNode);

        Object value = longConfigNode.getValue();

        assertNotNull(value);
        var longValue = assertInstanceOf(Long.class, value);

        assertEquals(21474836471L, longValue);



    }


}