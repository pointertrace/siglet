package com.siglet.container.config.siglet;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.modifiable.trace.ModifiableSpan;
import com.siglet.api.modifiable.trace.ModifiableSpanlet;
import com.siglet.api.parser.Node;
import com.siglet.api.parser.NodeChecker;
import com.siglet.api.parser.NodeCheckerFactory;
import com.siglet.api.parser.located.Located;
import com.siglet.api.parser.located.Location;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.config.siglet.parser.SigletConfigParser;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.parser.schema.SchemaFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class SigletConfigParserTest {


    private SigletConfigParser sigletConfigParser;


    @BeforeEach
    public void setUp() {

        sigletConfigParser = new SigletConfigParser();

    }

    @Test
    void parse() {

        String config = """
                name: mock spanlet
                siglet-class: com.siglet.pipeline.processor.siglet.SigletConfigParserTest$SpanletMock
                checker-factory-class: com.siglet.pipeline.processor.siglet.SigletConfigParserTest$ConfigFactoryMock
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


        YamlParser configParser = new YamlParser();

        Node node = configParser.parse("value: a valid value");

        sigletConfig.configChecker().check(node);


        SpanletConfigMock spanletConfigMock = node.getValue(SpanletConfigMock.class);

        assertNotNull(spanletConfigMock);
        assertEquals("a valid value", spanletConfigMock.getValue());
        assertEquals(Location.of(1, 8), spanletConfigMock.getValueLocation());


    }


    public static class SpanletMock implements ModifiableSpanlet<SpanletConfigMock> {

        @Override
        public Result span(ModifiableSpan modifiableSpan, ProcessorContext<SpanletConfigMock> spanletConfigMock,
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