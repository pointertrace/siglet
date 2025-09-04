package io.github.pointertrace.siglet.container.config.siglet.springboot;

import io.github.pointertrace.siglet.container.SigletError;
import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.launch.LaunchedClassLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SpringBootClassLoader extends LaunchedClassLoader {

    private static final String[] ALWAYS_PARENT_FIRST = new String[]{
            "java.", "javax.", "jdk.", "sun.",
            "org.springframework.boot.loader.",
            "org.springframework.boot.loader.launch.",
            "org.springframework.boot.loader.net.",
            "io.github.pointertrace.siglet.api",
            "io.github.pointertrace.siglet.parser"
    };

    private final String name;

    public static SpringBootClassLoader of(Archive root, ClassLoader parent) {

        try {
            Set<URL> urls = root.getClassPathUrls(entry -> {
                String n = entry.name();
                return n.startsWith("BOOT-INF/classes/") ||
                       (n.startsWith("BOOT-INF/lib/") && n.endsWith(".jar"));
            });

            urls.add(new File(root.toString()).toURI().toURL());

            return new SpringBootClassLoader(root.isExploded(), root, urls.toArray(URL[]::new), parent);
        } catch (IOException e) {
            throw new SigletError("Error creating SpringBoot classloader. "+e.getMessage(),e);
        }
    }

    protected SpringBootClassLoader(boolean exploded,
                                    Archive rootArchive,
                                    URL[] urls,
                                    ClassLoader parent) {
        super(exploded, rootArchive, urls, parent);
        this.name = "springboot-uberjar:" + rootArchive.toString();
    }

    private static boolean shouldBeParentFirst(String className) {
        for (String p : ALWAYS_PARENT_FIRST) {
            if (className.startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {

            Class<?> c = findLoadedClass(name);
            if (c != null) {
                if (resolve) resolveClass(c);
                return c;
            }

            if (shouldBeParentFirst(name)) {
                Class<?> parentLoaded = getParent().loadClass(name);
                if (resolve) resolveClass(parentLoaded);
                return parentLoaded;
            }

            try {
                try {
                    definePackageIfNecessary(name);
                } catch (Throwable ignore) {
                }
                Class<?> local = findClass(name);
                if (resolve) resolveClass(local);
                return local;
            } catch (ClassNotFoundException ignore) {

                Class<?> parentLoaded = getParent().loadClass(name);
                if (resolve) resolveClass(parentLoaded);
                return parentLoaded;
            }
        }
    }

    @Override
    public URL getResource(String name) {
        URL local = super.findResource(name);
        if (local != null) return local;
        return (getParent() != null) ? getParent().getResource(name) : null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> result = new ArrayList<>();

        Enumeration<URL> local = super.findResources(name);
        while (local.hasMoreElements()) result.add(local.nextElement());

        if (getParent() != null) {
            Enumeration<URL> parent = getParent().getResources(name);
            while (parent.hasMoreElements()) {
                URL u = parent.nextElement();
                if (!result.contains(u)) result.add(u);
            }
        }
        return Collections.enumeration(result);
    }

    public String getName() {
        return name;
    }
}
