package com.siglet.config;

import java.util.List;

public class Process extends Node {

    private Node to;

    public Process(String name, Node to) {
        super(name);
        this.to = to;
    }

    public Node getTo() {
        return to;
    }
}
