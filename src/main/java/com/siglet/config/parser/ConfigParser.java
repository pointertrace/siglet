package com.siglet.config.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.siglet.config.parser.locatednode.LocatedJsonNodeFactory;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.LocatedNodeConfigNodeTranslator;

import java.io.IOException;

public class ConfigParser {

    public ConfigNode parse(String config) {

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            LocatedJsonNodeFactory jsonNodeFactory = new LocatedJsonNodeFactory();

            mapper.setNodeFactory(jsonNodeFactory);
            JsonParser jsonParser = mapper.createParser(config);
            jsonNodeFactory.setParser(jsonParser);

            return LocatedNodeConfigNodeTranslator.translate(jsonParser.readValueAsTree());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
