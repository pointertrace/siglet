package com.siglet.container.engine.receiver.debug;

import java.util.HashMap;
import java.util.Map;

public class DebugReceivers {

    public final static DebugReceivers INSTANCE = new DebugReceivers();

    private final Map<String, DebugReceiver> receivers = new HashMap<String, DebugReceiver>();

    private DebugReceivers() {
    }


    public DebugReceiver get(String name) {
        return receivers.get(name);
    }

    protected void add(DebugReceiver debugReceiver) {
        receivers.put(debugReceiver.getName(),debugReceiver);
    }



}
