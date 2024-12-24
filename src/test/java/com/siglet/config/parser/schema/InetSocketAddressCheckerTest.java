package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class InetSocketAddressCheckerTest {

    String expected;

    @Test
    void describe() {

        TextChecker textChecker = new TextChecker(new InetSocketAddressChecker());

        expected = """
                text
                  inetSocketAddress""";

        assertEquals(expected, textChecker.describe());
    }

    @Test
    void getValue() {

        TextChecker textChecker = new TextChecker(new InetSocketAddressChecker());


        ConfigParser parser = new ConfigParser();

        Node inetAddressNode = parser.parse("localhost:8080");

        textChecker.check(inetAddressNode);

        Object value = inetAddressNode.getValue();

        assertNotNull(value);
        var inetAddress = assertInstanceOf(ValueItem.class, value);

        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), inetAddress.getValue());


    }


}