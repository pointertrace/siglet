package com.siglet.cli;

import com.siglet.SigletError;
import com.siglet.pipeline.common.processor.groovy.ShellCreator;
import org.apache.camel.CamelContext;

import java.util.Map;
import java.util.function.Supplier;

public class SigletContext {

    private static SigletContext instance;

    private final Supplier<CamelContext> contextSupplier;

    private final Map<String,String> receiverConsumers;

    private final ShellCreator shellCreator;

    private SigletContext(Supplier<CamelContext> contextSupplier, Map<String, String> receiverConsumers) {
        this.contextSupplier = contextSupplier;
        this.receiverConsumers = receiverConsumers;
        this.shellCreator = new ShellCreator();

    }

    public static synchronized SigletContext getInstance() {
        if (instance == null) {
            throw new SigletError("Context not initialized yet!");
        }
        return instance;
    }

    public static synchronized void init(Supplier<CamelContext> contextSupplier, Map<String,String> receiversUris) {
        if (instance != null) {
            throw new SigletError("Context already initialized!");
        }
        instance = new SigletContext(contextSupplier,receiversUris);
    }


    public Supplier<CamelContext> getContextSupplier() {
        return contextSupplier;
    }

    public Map<String, String> getReceiverConsumers() {
        return receiverConsumers;
    }

    public ShellCreator getShellCreator() {
        return shellCreator;
    }
}
