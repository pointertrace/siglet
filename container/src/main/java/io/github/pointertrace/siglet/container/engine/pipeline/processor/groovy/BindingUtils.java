package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy;

import groovy.lang.Binding;
import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.Signal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindingUtils {

    private static final String BINDING_RESULT = "result";

    private static final String BINDING_SIGNAL = "signal";

    private static final String BINDING_CONTEXT = "context";

    private static final String BINDING_ROUTES = "__siglet_routes";


    public static Result getResult(Binding binding) {
        return (Result) binding.getVariable(BINDING_RESULT);
    }

    public static void setResult(Binding binding, Result result) {
        binding.setVariable(BINDING_RESULT, result);
    }

    public static Signal getSignal(Binding binding) {
        Signal signal = (Signal) binding.getVariable(BINDING_SIGNAL);
        if (signal == null) {
           throw new IllegalStateException("Signal not found in binding");
        }
        return signal;
    }

    public static void setSignal(Binding binding, Signal signal) {
        binding.setVariable(BINDING_SIGNAL, signal);
    }

    public static Object getContext(Binding binding) {
        return binding.getVariable(BINDING_CONTEXT);
    }
    public static void setContext(Binding binding, ProcessorContext<?> context) {
        binding.setVariable(BINDING_CONTEXT, context);
    }

    public static Map<String, List<Signal>> getRoutes(Binding binding) {
        return (Map<String, List<Signal>>) binding.getVariable(BINDING_ROUTES);
    }

    public static void addRoute(Binding binding, String destination, Signal signal) {
        getRoutes(binding).computeIfAbsent(destination, s -> new ArrayList<>()).add(signal);
    }

    public static void createRoutes( Binding binding){
        binding.setVariable(BINDING_ROUTES,new HashMap<String,Signal>());
    }

}
