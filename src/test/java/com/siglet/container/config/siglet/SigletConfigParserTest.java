package com.siglet.container.config.siglet;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.signal.trace.Span;
import com.siglet.api.signal.trace.Spanlet;
import com.siglet.container.config.siglet.parser.SigletConfigParser;
import com.siglet.parser.Node;
import com.siglet.parser.NodeChecker;
import com.siglet.parser.NodeCheckerFactory;
import com.siglet.parser.YamlParser;
import com.siglet.parser.located.Located;
import com.siglet.parser.located.Location;
import com.siglet.parser.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.parser.schema.SchemaFactory.*;
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
                name: mock spanlet
                siglet-class: com.siglet.container.config.siglet.SigletConfigParserTest$SpanletMock
                checker-factory-class: com.siglet.container.config.siglet.SigletConfigParserTest$ConfigFactoryMock
                description: mock spanlet description
                """;


        SigletConfig sigletConfig = sigletConfigParser.parse(config);

        assertEquals("mock spanlet", sigletConfig.name());
        assertEquals(Location.of(1, 7), sigletConfig.nameLocation());
        assertEquals(SpanletMock.class, sigletConfig.sigletClass());
        assertEquals(Location.of(2, 15), sigletConfig.sigletLocation());
        assertInstanceOf(NodeChecker.class, sigletConfig.configChecker());
        assertEquals(Location.of(3, 24), sigletConfig.configCheckerFactoryClassLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertTrue(sigletConfig.destinations().isEmpty());


        YamlParser configParser = new YamlParser();

        Node node = configParser.parse("value: a valid value");

        sigletConfig.configChecker().check(node);


        SpanletConfigMock spanletConfigMock = node.getValue(SpanletConfigMock.class);

        assertNotNull(spanletConfigMock);
        assertEquals("a valid value", spanletConfigMock.getValue());
        assertEquals(Location.of(1, 8), spanletConfigMock.getValueLocation());


    }

    @Test
    void parse_nullConfiguration() {

        String config = """
                name: mock spanlet
                siglet-class: com.siglet.container.config.siglet.SigletConfigParserTest$SpanletMock
                description: mock spanlet description
                """;


        SigletConfig sigletConfig = sigletConfigParser.parse(config);

        assertEquals("mock spanlet", sigletConfig.name());
        assertEquals(Location.of(1, 7), sigletConfig.nameLocation());
        assertEquals(SpanletMock.class, sigletConfig.sigletClass());
        assertEquals(Location.of(2, 15), sigletConfig.sigletLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertTrue(sigletConfig.destinations().isEmpty());
        assertNull(sigletConfig.configChecker());


    }

    @Test
    void parse_destinations() {

        String config = """
                name: mock spanlet
                siglet-class: com.siglet.container.config.siglet.SigletConfigParserTest$SpanletMock
                checker-factory-class: com.siglet.container.config.siglet.SigletConfigParserTest$ConfigFactoryMock
                description: mock spanlet description
                destinations:
                  - first
                  - second
                """;


        SigletConfig sigletConfig = sigletConfigParser.parse(config);

        assertEquals("mock spanlet", sigletConfig.name());
        assertEquals(Location.of(1, 7), sigletConfig.nameLocation());
        assertEquals(SpanletMock.class, sigletConfig.sigletClass());
        assertEquals(Location.of(2, 15), sigletConfig.sigletLocation());
        assertInstanceOf(NodeChecker.class, sigletConfig.configChecker());
        assertEquals(Location.of(3, 24), sigletConfig.configCheckerFactoryClassLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertEquals(2, sigletConfig.destinations().size());
        assertEquals("first", sigletConfig.destinations().getFirst().getValue());
        assertEquals(Location.of(6, 5), sigletConfig.destinations().getFirst().getLocation());
        assertEquals("second", sigletConfig.destinations().get(1).getValue());
        assertEquals(Location.of(7, 5), sigletConfig.destinations().get(1).getLocation());


        YamlParser configParser = new YamlParser();

        Node node = configParser.parse("value: a valid value");

        sigletConfig.configChecker().check(node);


        SpanletConfigMock spanletConfigMock = node.getValue(SpanletConfigMock.class);

        assertNotNull(spanletConfigMock);
        assertEquals("a valid value", spanletConfigMock.getValue());
        assertEquals(Location.of(1, 8), spanletConfigMock.getValueLocation());


    }

    public static class SpanletMock implements Spanlet<SpanletConfigMock> {

        @Override
        public Result span(Span modifiableSpan, ProcessorContext<SpanletConfigMock> spanletConfigMock,
                           ResultFactory resultFactory) {
            return resultFactory.proceed();
        }

    }


    public static class SpanletConfigMock implements Located {

        private String value;

        private Location valueLocation;

        private Location location;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Location getValueLocation() {
            return valueLocation;
        }

        public void setValueLocation(Location valueLocation) {
            this.valueLocation = valueLocation;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public void setLocation(Location location) {
            this.location = location;
        }

    }


    public static class ConfigFactoryMock implements NodeCheckerFactory {
        @Override
        public NodeChecker create() {
            return strictObject(SpanletConfigMock::new,
                    requiredProperty(SpanletConfigMock::setValue, SpanletConfigMock::setValueLocation, "value", text()));
        }
    }

}