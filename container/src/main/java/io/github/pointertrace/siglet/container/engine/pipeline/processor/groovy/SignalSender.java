package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy;

import groovy.lang.Binding;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.Signal;

public class SignalSender {

    private final Signal signal;

    private final Binding binding;

    public SignalSender(Signal signal, Binding binding) {
        this.signal = signal;
        this.binding = binding;
    }

    public void to(String destination) {
        BindingUtils.addRoute(binding, destination, signal);
    }

}
