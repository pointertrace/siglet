package com.siglet.pipeline.common.processor.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

public class ShellCreator {

    private final Class<?> scriptBaseClass;

    protected ShellCreator(Class<?> scriptBaseClass) {
        this.scriptBaseClass = scriptBaseClass;
    }

    public ShellCreator() {
        this.scriptBaseClass = ScriptBaseClass.class;
    }

    public Script prepareScript(Script script, Object thisSignal) {
        script.getBinding().setProperty("thisSignal", thisSignal);
        return script;
    }

    public Script compile(String scriptText) {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(scriptBaseClass.getName());
        return new GroovyShell(scriptBaseClass.getClassLoader(), config).parse(scriptText);
    }
}
