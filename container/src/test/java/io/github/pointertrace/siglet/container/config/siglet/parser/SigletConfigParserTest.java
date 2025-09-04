package io.github.pointertrace.siglet.container.config.siglet.parser;

import io.github.pointertrace.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SigletConfigParserTest {


    private SigletConfigParser sigletConfigParser;


    @BeforeEach
    void setUp() {

        sigletConfigParser = new SigletConfigParser();

    }

    @Test
    void parse() {

        String config = """
                siglets:
                - name: mock spanlet
                  siglet-class: io.github.pointertrace.siglet.Spanlet
                  checker-factory-class: io.github.pointertrace.siglet.CheckerFactory
                  description: mock spanlet description
                - name: other mock spanlet
                  siglet-class: io.github.pointertrace.siglet.OtherSpanlet
                  checker-factory-class: io.github.pointertrace.siglet.OtherCheckerFactory
                  description: mock spanlet description
                """;


        SigletsConfig sigletsConfig = sigletConfigParser.parse(config);

        assertEquals(2, sigletsConfig.sigletsConfig().size());

        SigletConfig sigletConfig = sigletsConfig.sigletsConfig().getFirst();

        assertEquals("mock spanlet", sigletConfig.name());
        assertEquals(Location.of(2, 9), sigletConfig.nameLocation());
        assertEquals("io.github.pointertrace.siglet.Spanlet", sigletConfig.sigletClassName());
        assertEquals(Location.of(3, 17), sigletConfig.sigletLocation());
        assertEquals("io.github.pointertrace.siglet.CheckerFactory", sigletConfig.configCheckerFactoryClassName());
        assertEquals(Location.of(4, 26), sigletConfig.configCheckerFactoryClassLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertTrue(sigletConfig.destinations().isEmpty());

        sigletConfig = sigletsConfig.sigletsConfig().get(1);

        assertEquals("other mock spanlet", sigletConfig.name());
        assertEquals(Location.of(6, 9), sigletConfig.nameLocation());
        assertEquals("io.github.pointertrace.siglet.OtherSpanlet", sigletConfig.sigletClassName());
        assertEquals(Location.of(7, 17), sigletConfig.sigletLocation());
        assertEquals("io.github.pointertrace.siglet.OtherCheckerFactory", sigletConfig.configCheckerFactoryClassName());
        assertEquals(Location.of(8, 26), sigletConfig.configCheckerFactoryClassLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertTrue(sigletConfig.destinations().isEmpty());

    }

    @Test
    void parse_nullConfiguration() {

        String config = """
                siglets:
                - name: mock spanlet
                  siglet-class: io.github.pointertrace.siglet.Spanlet
                  description: mock spanlet description
                """;

        SigletsConfig sigletsConfig = sigletConfigParser.parse(config);

        assertEquals(1, sigletsConfig.sigletsConfig().size());

        SigletConfig sigletConfig = sigletsConfig.sigletsConfig().getFirst();

        assertEquals("mock spanlet", sigletConfig.name());
        assertEquals(Location.of(2, 9), sigletConfig.nameLocation());
        assertEquals("io.github.pointertrace.siglet.Spanlet", sigletConfig.sigletClassName());
        assertEquals(Location.of(3, 17), sigletConfig.sigletLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertTrue(sigletConfig.destinations().isEmpty());
        assertNull(sigletConfig.configCheckerFactoryClassName());


    }

    @Test
    void parse_destinations() {

        String config = """
                siglets:
                - name: mock spanlet
                  siglet-class: io.github.pointertrace.siglet.Siglet
                  checker-factory-class: io.github.pointertrace.siglet.CheckerFactory
                  description: mock spanlet description
                  destinations:
                  - first
                  - second
                """;


        SigletsConfig sigletsConfig = sigletConfigParser.parse(config);

        assertEquals(1, sigletsConfig.sigletsConfig().size());

        SigletConfig sigletConfig = sigletsConfig.sigletsConfig().getFirst();

        assertEquals("mock spanlet", sigletConfig.name());
        assertEquals(Location.of(2, 9), sigletConfig.nameLocation());
        assertEquals("io.github.pointertrace.siglet.Siglet", sigletConfig.sigletClassName());
        assertEquals(Location.of(3, 17), sigletConfig.sigletLocation());
        assertEquals("io.github.pointertrace.siglet.CheckerFactory", sigletConfig.configCheckerFactoryClassName());
        assertEquals(Location.of(4, 26), sigletConfig.configCheckerFactoryClassLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertEquals(2, sigletConfig.destinations().size());
        assertEquals("first", sigletConfig.destinations().getFirst().getValue());
        assertEquals(Location.of(7, 5), sigletConfig.destinations().getFirst().getLocation());
        assertEquals("second", sigletConfig.destinations().get(1).getValue());
        assertEquals(Location.of(8, 5), sigletConfig.destinations().get(1).getLocation());


    }

}