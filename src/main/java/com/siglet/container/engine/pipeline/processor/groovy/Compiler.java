package com.siglet.container.engine.pipeline.processor.groovy;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Signal;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
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

    public void prepareScript(Script script, Signal signal) {
       script.getBinding().setVariable("signal", signal);
    }

    public void prepareScript(Script script, Signal signal, ProcessorContext<?> context) {
        script.getBinding().setVariable("signal", signal);
        script.getBinding().setVariable("context", context);
    }
}
