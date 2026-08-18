package io.github.pointertrace.siglet.impl.engine.component.config;

import io.github.pointertrace.siglet.impl.config.descriptor.ConfigurableDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.BaseNode;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiConsumer;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

class ComponentTypeRegistryTest {

    private ComponentTypeRegistry<ComponentType<?, ?>> componentTypeRegistry;

    private DescriptionMock descriptionMock;

    @BeforeEach
    void setUp() {
        componentTypeRegistry = new ComponentTypeRegistry<>();
        componentTypeRegistry.register(new ComponentTypeMock());
        descriptionMock = new DescriptionMock();
    }

    @Test
    void createNameAndTypeSetter() {

        BiConsumer<DescriptionMock, StringValue> nameAndTypeSetter = ComponentTypeRegistry
                .createNameAndTypeSetter(DescriptionMock::setName, DescriptionMock::setType, "mock-type");

        nameAndTypeSetter.accept(descriptionMock, new StringValue("mock-name"));

        assertEquals("mock-name", descriptionMock.getName().getValue());
        assertEquals("mock-type", descriptionMock.getType().getValue());

    }

    @Test
    void parse() {
        String yaml = """
                mock-type: mock-name
                config:
                  config-property: config-property-value
                """;

       Schema schema = object(DescriptionMock::new)
                .addProperty(componentTypeRegistry.<DescriptionMock>getPropertySwitchSchema(DescriptionMock::setName,
                        DescriptionMock::setType));

        Node parsed = Parser.DEFAULT.parse(yaml);

        Factory factory = schema.validate(parsed);

        DescriptionMock descriptionMock = assertInstanceOf(DescriptionMock.class, factory.create(Object.class));

        assertEquals("mock-name", descriptionMock.getName().getValue());
        assertEquals(Location.of(1, 12), descriptionMock.getName().getLocation());
        assertEquals("mock-type", descriptionMock.getType().getValue());
        assertEquals(Location.of(1, 1), descriptionMock.getType().getLocation());

        assertNotNull(descriptionMock.getConfig());
        ConfigMock configMock = assertInstanceOf(ConfigMock.class, descriptionMock.getConfig());
        assertEquals("config-property-value", configMock.getProperty().getValue());
        assertEquals(Location.of(3, 20), configMock.getProperty().getLocation());

    }


    public static class DescriptionMock extends ConfigurableDescriptor {

        public void setType(StringValue type) {
            super.setType(type);
        }
    }

    public static class ConfigMock {

        private StringValue property;

        public StringValue getProperty() {
            return property;
        }

        public void setProperty(StringValue property) {
            this.property = property;
        }

    }

    public static class ComponentTypeMock implements ComponentType<ConfigMock, BaseNode> {

        @Override
        public String getType() {
            return "mock-type";
        }

        @Override
        public ConfigurationFactory<ConfigMock> getConfigurationFactory() {
            return ConfigurationFactory.of(
                    List.of(
                            property("config-property", ConfigMock::setProperty,stringValueObject())
                    ), ConfigMock.class
            );
        }

        @Override
        public ComponentCreator<BaseNode> getComponentCreator() {
            throw new UnsupportedOperationException();
        }
    }
}