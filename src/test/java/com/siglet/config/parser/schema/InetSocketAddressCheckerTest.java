package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class InetSocketAddressCheckerTest {


    @Test
    public void getValue() throws Exception {

        TextChecker textChecker = new TextChecker(new InetSocketAddressChecker());


        ConfigParser parser = new ConfigParser();

        ConfigNode inetAddressConfigNode = parser.parse("localhost:8080");

        textChecker.check(inetAddressConfigNode);

        Object value = inetAddressConfigNode.getValue();

        assertNotNull(value);
        var inetAddress = assertInstanceOf(ValueItem.class, value);

        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), inetAddress.getValue());


    }


}