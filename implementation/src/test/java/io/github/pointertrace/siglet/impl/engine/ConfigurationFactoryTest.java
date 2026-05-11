package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.api.SigletConfigFactory;
import io.github.pointertrace.siglet.api.SigletConfigParserFactory;
import io.github.pointertrace.siglet.parser.*;
import io.github.pointertrace.siglet.parser.impl.schema.SchemaPropertyBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.stringValueObject;
import static org.junit.jupiter.api.Assertions.*;

class ConfigurationFactoryTest {


    @Test
    void emptyConfig() {

        ConfigurationFactory<Void> emptyConfigFactory = ConfigurationFactory.of();

        assertTrue(emptyConfigFactory.createConfigSchema().isEmpty());

    }

    @Test
    void sigletConfigFactory() {

        String yaml = """
                property: value
                """;

        ConfigurationFactory<SimpleConfig> sigletConfigFactory = ConfigurationFactory.of(new SimpleSigletConfigFactory());

        Optional<Schema.Builder<?, SimpleConfig>> schemaBuilderOpt = sigletConfigFactory.createConfigSchema();

        assertTrue(schemaBuilderOpt.isPresent());

        Schema.Builder<?, ?> schemaBuilder = schemaBuilderOpt.get();
        Node node = Parser.DEFAULT.parse(yaml);
        Factory factory = schemaBuilder.build().validate(node);
        SimpleConfig simpleConfig = factory.create(SimpleConfig.class);

        assertEquals("property: value", simpleConfig.getValue().getValue());
    }

    @Test
    void schemaConfigFactory() {

        String yaml = """
                property: value
                """;

        ConfigurationFactory<SimpleConfig> sigletConfigFactory = ConfigurationFactory.of(new SimpleSchemaConfigFactory());

        Optional<Schema.Builder<?, SimpleConfig>> schemaBuilderOpt = sigletConfigFactory.createConfigSchema();

        assertTrue(schemaBuilderOpt.isPresent());

        Schema.Builder<?, ?> schemaBuilder = schemaBuilderOpt.get();
        Node node = Parser.DEFAULT.parse(yaml);
        Factory factory = schemaBuilder.build().validate(node);
        SimpleConfig simpleConfig = factory.create(SimpleConfig.class);

        assertEquals("value", simpleConfig.getValue().getValue());
    }

    @Test
    void propertyListConfigClass() {

        String yaml = """
                property: value
                """;

        ConfigurationFactory<SimpleConfig> sigletConfigFactory = ConfigurationFactory.of(
                List.of(property("property", SimpleConfig::setValue, stringValueObject())),
                SimpleConfig.class
        );

        Optional<Schema.Builder<?, SimpleConfig>> schemaBuilderOpt = sigletConfigFactory.createConfigSchema();

        assertTrue(schemaBuilderOpt.isPresent());

        Schema.Builder<?, ?> schemaBuilder = schemaBuilderOpt.get();
        Node node = Parser.DEFAULT.parse(yaml);
        Factory factory = schemaBuilder.build().validate(node);
        SimpleConfig simpleConfig = factory.create(SimpleConfig.class);

        assertEquals("value", simpleConfig.getValue().getValue());
    }

    static class SimpleSchemaConfigFactory implements SigletConfigParserFactory<SimpleConfig> {

        @Override
        public List<SchemaPropertyBuilder<SimpleConfig, ?>> createConfigSchema() {
            return List.of(property("property", SimpleConfig::setValue, stringValueObject()));
        }

        @Override
        public Class<SimpleConfig> getConfigClass() {
            return SimpleConfig.class;
        }
    }

    static class SimpleSigletConfigFactory implements SigletConfigFactory<SimpleConfig> {

        @Override
        public SimpleConfig createConfig(String yaml) {
            SimpleConfig simpleConfig = new SimpleConfig();
            simpleConfig.setValue(new StringValue(yaml));
            return simpleConfig;
        }

    }

    static class SimpleConfig {
        private StringValue value;

        public StringValue getValue() {
            return value;
        }

        private void setValue(StringValue value) {
            this.value = value;
        }
    }

}