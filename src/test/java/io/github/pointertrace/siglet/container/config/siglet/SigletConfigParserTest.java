package io.github.pointertrace.siglet.container.config.siglet;

import io.github.pointertrace.siget.parser.Node;
import io.github.pointertrace.siget.parser.NodeChecker;
import io.github.pointertrace.siget.parser.NodeCheckerFactory;
import io.github.pointertrace.siget.parser.YamlParser;
import io.github.pointertrace.siget.parser.located.Located;
import io.github.pointertrace.siget.parser.located.Location;
import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.config.siglet.parser.SigletConfigParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.pointertrace.siget.parser.schema.SchemaFactory.*;
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
                  siglet-class: io.github.pointertrace.siglet.container.config.siglet.SigletConfigParserTest$SpanletMock
                  checker-factory-class: io.github.pointertrace.siglet.container.config.siglet.SigletConfigParserTest$ConfigFactoryMock
                  description: mock spanlet description
                - name: other mock spanlet
                  siglet-class: io.github.pointertrace.siglet.container.config.siglet.SigletConfigParserTest$SpanletMock
                  checker-factory-class: io.github.pointertrace.siglet.container.config.siglet.SigletConfigParserTest$ConfigFactoryMock
                  description: mock spanlet description
                """;


        SigletsConfig sigletsConfig = sigletConfigParser.parse(config);

        assertEquals(2, sigletsConfig.sigletsConfig().size());

        SigletConfig sigletConfig = sigletsConfig.sigletsConfig().getFirst();

        assertEquals("mock spanlet", sigletConfig.name());
        assertEquals(Location.of(2, 9), sigletConfig.nameLocation());
        assertEquals(SpanletMock.class, sigletConfig.sigletClass());
        assertEquals(Location.of(3, 17), sigletConfig.sigletLocation());
        assertInstanceOf(NodeChecker.class, sigletConfig.configChecker());
        assertEquals(Location.of(4, 26), sigletConfig.configCheckerFactoryClassLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertTrue(sigletConfig.destinations().isEmpty());

        sigletConfig = sigletsConfig.sigletsConfig().get(1);

        assertEquals("other mock spanlet", sigletConfig.name());
        assertEquals(Location.of(6, 9), sigletConfig.nameLocation());
        assertEquals(SpanletMock.class, sigletConfig.sigletClass());
        assertEquals(Location.of(7, 17), sigletConfig.sigletLocation());
        assertInstanceOf(NodeChecker.class, sigletConfig.configChecker());
        assertEquals(Location.of(8, 26), sigletConfig.configCheckerFactoryClassLocation());
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
                siglets:
                - name: mock spanlet
                  siglet-class: io.github.pointertrace.siglet.container.config.siglet.SigletConfigParserTest$SpanletMock
                  description: mock spanlet description
                """;

        SigletsConfig sigletsConfig = sigletConfigParser.parse(config);

        assertEquals(1, sigletsConfig.sigletsConfig().size());

        SigletConfig sigletConfig = sigletsConfig.sigletsConfig().getFirst();

        assertEquals("mock spanlet", sigletConfig.name());
        assertEquals(Location.of(2, 9), sigletConfig.nameLocation());
        assertEquals(SpanletMock.class, sigletConfig.sigletClass());
        assertEquals(Location.of(3, 17), sigletConfig.sigletLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertTrue(sigletConfig.destinations().isEmpty());
        assertNull(sigletConfig.configChecker());


    }

    @Test
    void parse_destinations() {

        String config = """
                siglets:
                - name: mock spanlet
                  siglet-class: io.github.pointertrace.siglet.container.config.siglet.SigletConfigParserTest$SpanletMock
                  checker-factory-class: io.github.pointertrace.siglet.container.config.siglet.SigletConfigParserTest$ConfigFactoryMock
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
        assertEquals(SpanletMock.class, sigletConfig.sigletClass());
        assertEquals(Location.of(3, 17), sigletConfig.sigletLocation());
        assertInstanceOf(NodeChecker.class, sigletConfig.configChecker());
        assertEquals(Location.of(4, 26), sigletConfig.configCheckerFactoryClassLocation());
        assertEquals("mock spanlet description", sigletConfig.description());
        assertNotNull(sigletConfig.destinations());
        assertEquals(2, sigletConfig.destinations().size());
        assertEquals("first", sigletConfig.destinations().getFirst().getValue());
        assertEquals(Location.of(7, 5), sigletConfig.destinations().getFirst().getLocation());
        assertEquals("second", sigletConfig.destinations().get(1).getValue());
        assertEquals(Location.of(8, 5), sigletConfig.destinations().get(1).getLocation());


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