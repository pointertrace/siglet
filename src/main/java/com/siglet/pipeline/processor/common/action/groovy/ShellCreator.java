package com.siglet.pipeline.processor.common.action.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

public class ShellCreator {

    private final Class<?> scriptBaseClass;

    public ShellCreator(Class<?> scriptBaseClass) {
        this.scriptBaseClass = scriptBaseClass;
    }

    public ShellCreator() {
        this.scriptBaseClass = ScriptBaseClass.class;
    }

    public Script prepareScript(Script script, Object thisSignal) {
        script.getBinding().setProperty(ScriptBaseClass.SIGNAL_INTRINSIC_VAR_NAME, thisSignal);
        return script;
    }

    public Script compile(String scriptText) {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(scriptBaseClass.getName());
        return new GroovyShell(scriptBaseClass.getClassLoader(), config).parse(scriptText);
    }
}
