package com.siglet.config;

import java.util.ArrayList;
import java.util.List;

public class Exporter extends Endpoint {

    private List<Process> in;


    public Exporter(String name, List<Process> in) {
        super(name);
        this.in = new ArrayList<>(in);
    }

    public List<Process> getIn() {
        return in;
    }
}
