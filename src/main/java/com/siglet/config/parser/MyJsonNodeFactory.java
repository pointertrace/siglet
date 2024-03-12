package com.siglet.config.parser;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;

public class MyJsonNodeFactory extends JsonNodeFactory  {

    public TextNode textNode(String text) {
        System.out.println("########## aqui ##########");
        return TextNode.valueOf(text);
    }
}
