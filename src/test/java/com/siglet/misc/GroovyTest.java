package com.siglet.misc;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Test;

public class GroovyTest {


    @Test
    public void executeScript() {
        String testScript = """
                sendTo "destination" greet {
                    name "este eh um nome"
                    sub {
                       description "este eh um sub"
                    }
                    100
                }
                
                """;

        var config = new CompilerConfiguration();
        config.setScriptBaseClass(MyBaseClass.class.getName());
        Script var = new GroovyShell(GroovyTest.class.getClassLoader(), config).parse(testScript);
        Proxy proxy = new Proxy();
        var.setProperty("proxy", proxy);

        var.run();
        System.out.println("name=" + proxy.getName());
    }

    public static class Destination {

        private final String destination;

        public Destination(String destination) {
            this.destination = destination;
        }

        public void greet(Closure<Integer> closure) {
            System.out.println(closure);
            closure.setResolveStrategy(Closure.DELEGATE_ONLY);
            Proxy proxy = (Proxy) closure.getProperty("proxy");
            closure.setDelegate(proxy);
            System.out.println("closure:"+ closure.call());
            System.out.println("sending [" + destination + "] name=" +  proxy.getName());
        }



    }


    public abstract static class MyBaseClass extends Script {
        String name;

        public MyBaseClass() {
        }

        public Destination destination(String destination) {
            return new Destination(destination);
        }

        public Destination sendTo(String destination) {
            return new Destination(destination);
        }


        public String greet(Closure<Void> closure) {
            System.out.println(closure);
            closure.setResolveStrategy(Closure.DELEGATE_ONLY);
            Proxy proxy = (Proxy) closure.getProperty("proxy");
            closure.setDelegate(proxy);
            closure.call();
            System.out.println("name=" + proxy.getName());
            return "returned";
        }

    }


    public static class Proxy {

        private String name;

        private SubProxy sub = new SubProxy();

        public void name(String name) {
            this.name = name;
            System.out.println("Proxy.name = " + name);
        }

        public SubProxy getSub() {
            return sub;
        }

        public void sub(Closure<Void> closure) {
            closure.getThisObject();
            System.out.println(closure);
            closure.setResolveStrategy(Closure.DELEGATE_ONLY);
            Proxy proxy = (Proxy) ((Script) closure.getThisObject()).getProperty("proxy");
            closure.setDelegate(proxy.getSub());
            closure.call();
        }

        public String getName() {
            return name;
        }
    }

    public static class SubProxy {

        public void description(String description) {
            System.out.println("Proxy.description = " + description);
        }

    }
}
