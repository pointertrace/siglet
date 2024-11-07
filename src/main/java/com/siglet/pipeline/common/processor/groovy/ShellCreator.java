package com.siglet.pipeline.common.processor.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

public class ShellCreator {

    private final Class<?> scriptBaseClass;

    protected ShellCreator(Class<?> scriptBaseClass) {
        this.scriptBaseClass = scriptBaseClass;
    }

    protected ShellCreator() {
        this.scriptBaseClass = ScriptBaseClass.class;
    }

    public Script createScript(String script, Object thisSignal) {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(scriptBaseClass.getName());
        Script parsedScript = new GroovyShell(scriptBaseClass.getClassLoader(), config).parse(script);
        parsedScript.getBinding().setProperty("thisSignal", thisSignal);
        return parsedScript;
    }
}
