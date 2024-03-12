package com.siglet.config.parser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.List;

public class ConfigNode {

    public static enum Type {
        NUMBER,

    }

    public static class LongNode extends ConfigNode {

        private int line;

        private int col;

        public LongNode value;

        public Type getType() {
            return Type.NUMBER;
        }

        public int getLine() {
            return line;
        }

        public int getCol() {
            return col;
        }

        public Long getValue() {
            return value.getValue();
        }

    }

    public static class ArrayNode extends ConfigNode {



    }


}
