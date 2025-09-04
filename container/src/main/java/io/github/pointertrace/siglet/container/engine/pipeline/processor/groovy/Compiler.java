package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Signal;
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
