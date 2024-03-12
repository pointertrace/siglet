package com.siglet.config.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class CustomJsonNodeDeserializer extends  JsonNodeDeserializer {

    @Override
    public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int lineNumber = p.getCurrentLocation().getLineNr(); // Obtenha o número da linha atual do JsonParser
        JsonNode node = super.deserialize(p, ctxt); // Chame o método de desserialização da classe pai

        // Adicione a informação da linha como um campo no JsonNode
        ((ObjectNode) node).put("lineNumber", lineNumber);

        return node;
    }}
