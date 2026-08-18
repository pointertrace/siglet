package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultImpl;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;

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

    /**
     * Creates a new Script instance from the same compiled class as the template.
     * Compilation (expensive) happened once when template was created;
     * this is a cheap object instantiation.
     */
    public Script newInstance(Script template) {
        return InvokerHelper.createScript(template.getClass(), new Binding());
    }

    public void prepareScript(Script script, Signal signal, Context<?> context) {
        ((ScriptBaseClass) script).init();
        BindingUtils.setResult(script.getBinding(), ResultImpl.proceed());
        BindingUtils.setSignal(script.getBinding(), signal);
        BindingUtils.setContext(script.getBinding(), context);
        BindingUtils.createRoutes(script.getBinding());
    }
}
