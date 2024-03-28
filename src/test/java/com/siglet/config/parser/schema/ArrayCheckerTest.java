package com.siglet.config.parser.schema;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrayCheckerTest {

    private ArrayChecker arrayChecker;

    private ConfigParser parser;

    @BeforeEach
    void setUp() {
        parser = new ConfigParser();
    }

    @Test
    public void array() throws SchemaValidationException {

        ConfigNode node = parser.parse("""
                - 1
                - 2
                """);

        arrayChecker = new ArrayChecker(new IntChecker());

        arrayChecker.check(node);


    }


    // todo ver esse teste!!!!!
    public void array_invalid() throws SchemaValidationException {

        ConfigNode node = parser.parse("""
                key: value
                """);

        arrayChecker = new ArrayChecker(new IntChecker());

        arrayChecker.check(node);


    }
}