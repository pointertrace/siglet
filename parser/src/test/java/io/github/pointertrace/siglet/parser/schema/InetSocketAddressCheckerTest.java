package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class InetSocketAddressCheckerTest {

    String expected;

    @Test
    void describe() {

        TextChecker textChecker = new TextChecker(new InetSocketAddressChecker());

        expected = "text\n" +
                   "  inetSocketAddress";

        assertEquals(expected, textChecker.describe());
    }

    @Test
    void getValue() {

        TextChecker textChecker = new TextChecker(new InetSocketAddressChecker());


        YamlParser parser = new YamlParser();

        Node inetAddressNode = parser.parse("localhost:8080");

        textChecker.check(inetAddressNode);

        Object value = inetAddressNode.getValue();

        assertNotNull(value);
        InetSocketAddress inetAddress = assertInstanceOf(InetSocketAddress.class, value);

        assertEquals(new InetSocketAddress( "localhost", 8080), inetAddress);


    }


}