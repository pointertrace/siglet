package com.siglet.config;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Receiver extends Endpoint {

    private final List<Process> out;

    public Receiver(String name, List<Process> out) {
        super(name);
        this.out = new ArrayList<>(out);
    }

    public List<Process> getOut() {
        return out;
    }
}
