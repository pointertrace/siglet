package io.github.pointertrace.siglet.impl.config.siglet.configfile;


import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.parser.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;


public class SigletConfigFile {

    private static final String MISSING_SIGLETS_ARRAY_PROPERTY_MESSAGE =
            "Missing siglets definitions:";

    private static final Schema SIGLET_CONFIG_FILE_SCHEMA = object(SigletConfigFile::new)
            .customErrorMessage("Error in siglets definitions:", "#location No siglet definitions in siglet-config.yaml")
            .addProperty(property("siglets", SigletConfigFile::setSigletDefinitions, array(ArrayList::new, arrayItem(List::add,
                                    object(SigletConfigFileDefinition::new)
                                            .customErrorMessage("Error in siglet at #location:", "#location Siglet object must be a object array")
                                            .addProperty(
                                                    property("name", SigletConfigFileDefinition::setName, stringValueObject().customErrorMessage("#location Siglet name must be a string"))
                                                            .customErrorMessage("Invalid siglet name at #location:", "#location Siglet must have a name property"))
                                            .addProperty(
                                                    property("description", SigletConfigFileDefinition::setDescription, stringValueObject().customErrorMessage("#location Siglet description must be a string"))
                                                            .customErrorMessage("Invalid siglet description at #location:", "#location Siglet must have a description property"))
                                            .addProperty(
                                                    property("siglet-class", SigletConfigFileDefinition::setSigletClassName, stringValueObject().customErrorMessage("#location siglet siglet-class must be a string"))
                                                            .customErrorMessage("Invalid siglet siglet-class at #location:", "#location Siglet must have a siglet-class property"))
                                            .addOptionalProperty(
                                                    property("config-parser-factory-class", SigletConfigFileDefinition::setConfigParserFactoryClassName, stringValueObject().customErrorMessage("#location Siglet config-parser-factory-class must be a string"))
                                                            .customErrorMessage("Invalid siglet config-parser-factory-class at #location:"))
                                            .addOptionalProperty(
                                                    property("config-factory-class", SigletConfigFileDefinition::setConfigFactoryClassName, stringValueObject().customErrorMessage("#location Siglet config-factory-class must be a string"))
                                                            .customErrorMessage("Invalid siglet config-factory-class at #location:"))
                                            .addOptionalProperty(
                                                    property("destinations", SigletConfigFileDefinition::setDestinations,
                                                            array(ArrayList::new, arrayItem(List::add, stringValueObject().customErrorMessage("#location Siglet destination must be a string")
                                                                    ).customErrorMessage("Error in siglets destination array item at #location:")
                                                            ).customErrorMessage("Error in siglets destination array at #location:","#location Siglet destinations must be an array")
                                                    ).customErrorMessage("Error in siglet destinations at #location:")
                                            )
                            ).customErrorMessage("Error in siglet array item:")
                    ).customErrorMessage("Error in siglet object array:", "#location siglets property must be an array")
            ).customErrorMessage("Error in siglets property:", "#location Could not find siglets property"));

    private List<SigletConfigFileDefinition> sigletConfigDefinitionConfigFileFiles;


    public List<SigletConfigFileDefinition> getSigletDefinitions() {
        return Collections.unmodifiableList(sigletConfigDefinitionConfigFileFiles);
    }

    void setSigletDefinitions(List<SigletConfigFileDefinition> sigletConfigDefinitionConfigFileFiles) {
        this.sigletConfigDefinitionConfigFileFiles = new ArrayList<>(sigletConfigDefinitionConfigFileFiles);
    }

    public static SigletConfigFile parse(String yaml) {
        if (yaml == null || yaml.isBlank()) {
            throw new SigletError("Siglet config file is empty");
        }

        try {
            Node sigletNode = Parser.DEFAULT.parse(yaml);
            Factory factory = SIGLET_CONFIG_FILE_SCHEMA.validate(sigletNode);
            SigletConfigFile sigletConfigFile = factory.create(SigletConfigFile.class);

            validateConfigDefinitionModes(sigletConfigFile);

            return sigletConfigFile;
        } catch (SchemaException e) {
            System.out.println("SchemaException message: " + e.getMessage());
            System.out.println("SchemaException class source: " +
                    e.getClass().getProtectionDomain().getCodeSource().getLocation());
            throw new SigletError(e.getMessage(), e);
        }


    }

    private static void validateConfigDefinitionModes(SigletConfigFile sigletConfigFile) {
        if (sigletConfigFile.getSigletDefinitions() == null) {
            return;
        }

        for (SigletConfigFileDefinition definition : sigletConfigFile.getSigletDefinitions()) {
            boolean hasConfigParserFactoryClass = definition.getConfigParserFactoryClassName() != null;
            boolean hasConfigFactoryClass = definition.getConfigFactoryClassName() != null;


            if (hasConfigFactoryClass && hasConfigParserFactoryClass) {
                throw new SigletError(String.format(
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

