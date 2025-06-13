package org.springframework.boot.loader.launch;


import com.siglet.api.modifiable.trace.ModifiableSpan;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Test {

    public static void main(String[] args) throws Exception {


        String jarPath = "/home/osvaldo/projects/SimpleSpanlet/target/SimpleSpanlet-1.0-SNAPSHOT.jar";

        File jarFile = new File(jarPath);
        // Cria um classloader com o JAR externo
        URL[] urls = {jarFile.toURI().toURL()};
        URLClassLoader classLoader = new URLClassLoader(urls, Test.class.getClassLoader());

        // Nome totalmente qualificado da classe que você quer carregar
        String className = "org.example.MySpanlet";

        // Carrega a classe
        Class<?> clazz = classLoader.loadClass(className);

        // Instancia a classe (assumindo construtor vazio)
        Object instance = clazz.getDeclaredConstructor().newInstance();

        System.out.println("Classe carregada: " + clazz);
        System.out.println("Instância criada: " + instance);

        Span span = Span.newBuilder()
                .setName("span-name")
                .build();

        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter(span, resource, instrumentationScope, true);

        Method method = instance.getClass().getMethod("span", ModifiableSpan.class, Void.class);

        method.invoke(instance, protoSpanAdapter, null);

        System.out.println("name:" + protoSpanAdapter.getName());

    }

}