package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultImpl;
import org.codehaus.groovy.control.CompilerConfiguration;

public class Compiler {

    private final Class<?> scriptBaseClass;

    public Compiler(Class<?> scriptBaseClass) {
        this.scriptBaseClass = scriptBaseClass;
    }

    public Compiler() {
        this.scriptBaseClass = ScriptBaseClass.class;
    }

    public Script compile(String scriptText) {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(scriptBaseClass.getName());
        return new GroovyShell(scriptBaseClass.getClassLoader(), config).parse(scriptText);
    }

    public void prepareScript(Script script, Signal signal, Context<?> context) {
        BindingUtils.setResult(script.getBinding(), ResultImpl.proceed());
        BindingUtils.setSignal(script.getBinding(), signal);
        BindingUtils.setContext(script.getBinding(), context);
        BindingUtils.createRoutes(script.getBinding());
    }
}
