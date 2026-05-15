package io.github.pointertrace.siglet.impl.config.siglet.configfile;


import io.github.pointertrace.siglet.parser.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;


public class SigletConfigFile {

    private static final Schema SIGLET_CONFIG_FILE_SCHEMA = object(SigletConfigFile::new)
            .addProperty(property("siglets", SigletConfigFile::setSigletDefinitions, array(ArrayList::new, arrayItem(List::add,
                    object(SigletConfigFileDefinition::new)
                            .addProperty(property("name", SigletConfigFileDefinition::setName, stringValueObject()))
                            .addProperty(property("description", SigletConfigFileDefinition::setDescription, stringValueObject()))
                            .addOptionalProperty(property("siglet-class", SigletConfigFileDefinition::setSigletClassName, stringValueObject()))
                            .addOptionalProperty(property("config-parser-factory-class", SigletConfigFileDefinition::setConfigParserFactoryClassName, stringValueObject()))
                            .addOptionalProperty(property("config-factory-class", SigletConfigFileDefinition::setConfigFactoryClassName, stringValueObject()))
                            .addOptionalProperty(property("destinations", SigletConfigFileDefinition::setDestinations,
                                    array(ArrayList::new, arrayItem(List::add, stringValueObject()))))))));

    private List<SigletConfigFileDefinition> sigletConfigDefinitionConfigFileFiles;


    public List<SigletConfigFileDefinition> getSigletDefinitions() {
        return Collections.unmodifiableList(sigletConfigDefinitionConfigFileFiles);
    }

    void setSigletDefinitions(List<SigletConfigFileDefinition> sigletConfigDefinitionConfigFileFiles) {
        this.sigletConfigDefinitionConfigFileFiles = new ArrayList<>(sigletConfigDefinitionConfigFileFiles);
    }

    public static SigletConfigFile parse(String yaml) {

        Node sigletNode = Parser.DEFAULT.parse(yaml);

        Factory factory = SIGLET_CONFIG_FILE_SCHEMA.validate(sigletNode);

        SigletConfigFile sigletConfigFile = factory.create(SigletConfigFile.class);

        validateConfigDefinitionModes(sigletConfigFile);

        return sigletConfigFile;
    }

    private static void validateConfigDefinitionModes(SigletConfigFile sigletConfigFile) {
        if (sigletConfigFile.getSigletDefinitions() == null) {
            return;
        }

        for (SigletConfigFileDefinition definition : sigletConfigFile.getSigletDefinitions()) {
            boolean hasConfigParserFactoryClass = definition.getConfigParserFactoryClassName() != null;
            boolean hasConfigFactoryClass = definition.getConfigFactoryClassName() != null;


            if (hasConfigFactoryClass && hasConfigParserFactoryClass) {
                throw new IllegalArgumentException(String.format(
                        "Siglet %s must define exactly one config mode: " +
                                "config-parser-factory-class or config-factory-class",
                        sigletName(definition)));
            }
        }
    }

    private static String sigletName(SigletConfigFileDefinition definition) {
        if (definition.getName() == null || definition.getName().getValue() == null) {
            return "<unknown>";
        }
        return definition.getName().getValue();
    }

    public static class SigletConfigFileDefinition {

        private StringValue name;

        private StringValue description;

        private StringValue sigletClassName;

        private StringValue configParserFactoryClassName;

        private StringValue configFactoryClassName;

        private List<StringValue> destinations;

        public StringValue getName() {
            return name;
        }

        private void setName(StringValue name) {
            this.name = name;
        }

        public StringValue getDescription() {
            return description;
        }

        private void setDescription(StringValue description) {
            this.description = description;
        }

        public StringValue getConfigParserFactoryClassName() {
            return configParserFactoryClassName;
        }

        protected void setSigletClassName(StringValue sigletClassName) {
            this.sigletClassName = sigletClassName;
        }

        public StringValue getSigletClassName() {
            return sigletClassName;
        }

        protected void setConfigParserFactoryClassName(StringValue configParserFactoryClassName) {
            this.configParserFactoryClassName = configParserFactoryClassName;
        }

        public StringValue getConfigFactoryClassName() {
            return configFactoryClassName;
        }

        protected void setConfigFactoryClassName(StringValue configFactoryClassName) {
            this.configFactoryClassName = configFactoryClassName;
        }

        public List<StringValue> getDestinations() {
            return destinations;
        }

        protected void setDestinations(List<StringValue> destinations) {
            this.destinations = destinations;
        }
    }
}

