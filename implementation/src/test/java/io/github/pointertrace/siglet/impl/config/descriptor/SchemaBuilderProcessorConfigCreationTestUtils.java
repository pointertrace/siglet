package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

class SchemaBuilderProcessorConfigCreationTestUtils {

    @Test
    void no_destination() {

        String yaml = """
                property: value
                """;

        Node node = Parser.DEFAULT.parse(yaml);

        Schema.Builder<?, Destination> builder = object(Destination::new)
                .addProperty(property("property", Destination::setProperty, string()))
                .addProperty(SchemaBuilderUtils.destinationSchemaBuilder("to", Destination::setTo));


        SchemaException sigletError = assertThrows(SchemaException.class, () -> builder.build().validate(node));

        assertEquals("Expecting object at (1,1):\n  Expecting Destination (to property) at (1,1)", sigletError.getMessage().trim());


    }

    @Test
    void destination_array() {

        String yaml = """
                property: value
                to:
                - destination1
                - destination2
                """;

        Node node = Parser.DEFAULT.parse(yaml);

        Schema.Builder<?, Destination> builder = object(Destination::new)
                .addProperty(property("property", Destination::setProperty, string()))
                .addProperty(SchemaBuilderUtils.destinationSchemaBuilder("to", Destination::setTo));

        Factory factory = builder.build().validate(node);

        Destination destination =  factory.create(Destination.class);
        assertNotNull(destination);
        assertEquals("value", destination.getProperty());
        assertNotNull(destination.getTo());
        assertEquals(2, destination.getTo().size());
        assertEquals("destination1", destination.getTo().get(0).getValue());
        assertEquals("destination2", destination.getTo().get(1).getValue());

    }

    @Test
    void destination_string() {

        String yaml = """
                property: value
                to: destination
                """;

        Node node = Parser.DEFAULT.parse(yaml);

        Schema.Builder<?, Destination> builder = object(Destination::new)
                .addProperty(property("property", Destination::setProperty, string()))
                .addProperty(SchemaBuilderUtils.destinationSchemaBuilder("to", Destination::setTo));


        Factory factory = builder.build().validate(node);

        Destination destination =  factory.create(Destination.class);
        assertNotNull(destination);
        assertEquals("value", destination.getProperty());
        assertNotNull(destination.getTo());
        assertEquals(1, destination.getTo().size());

    }

    @Test
    void destination_stringError() {

        String yaml = """
                property: value
                to: 1
                """;

        Node node = Parser.DEFAULT.parse(yaml);

        Schema.Builder<?, Destination> builder = object(Destination::new)
                .addProperty(property("property", Destination::setProperty, string()))
                .addProperty(SchemaBuilderUtils.destinationSchemaBuilder("to", Destination::setTo));

        SchemaException sigletError = assertThrows(SchemaException.class, () -> builder.build().validate(node));

        assertEquals("(2,1) Destination (to property) must be a string or a list of strings", sigletError.getMessage().trim());

    }

    @Test
    void destination_arrayError() {

        String yaml = """
                property: value
                to:
                - 1
                """;


        Node node = Parser.DEFAULT.parse(yaml);

        Schema.Builder<?, Destination> builder = object(Destination::new)
                .addProperty(property("property", Destination::setProperty, string()))
                .addProperty(SchemaBuilderUtils.destinationSchemaBuilder("to", Destination::setTo));

        SchemaException sigletError = assertThrows(SchemaException.class, () -> builder.build().validate(node));

        assertEquals("(2,1) Destination (to property) must be a string or a list of strings", sigletError.getMessage().trim());

    }


    public static class Destination {

        private String property;

        private List<StringValue> to;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public List<StringValue> getTo() {
            return to;
        }

        public void setTo(List<StringValue> to) {
            this.to = to;
        }
    }
}