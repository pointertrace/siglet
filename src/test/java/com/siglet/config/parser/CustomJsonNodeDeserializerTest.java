package com.siglet.config.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import groovyjarjarantlr4.v4.codegen.ParserFactory;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CustomJsonNodeDeserializerTest {


    @Test
    public void test() throws Exception {
        // Arquivo YAML para fazer o parse
        File yamlFile = new File("seuarquivo.yaml");

        // Criação do ObjectMapper e JsonFactory
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
         mapper.setNodeFactory(new CustomJsonNodeFactory(mapper.getNodeFactory(),new CustomParserFactory()));

        String x = """
                objeto:
                  key: 1
                  obke: 2
                  filho :
                    - 1
                    - 2
                """;
        // Processando o arquivo
        JsonNode node = mapper.readTree(x);
        System.out.println(node);
    }
}