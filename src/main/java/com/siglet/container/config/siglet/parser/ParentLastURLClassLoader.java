package com.siglet.container.config.siglet.parser;

import java.net.URL;
import java.net.URLClassLoader;

public class ParentLastURLClassLoader extends URLClassLoader {

    public ParentLastURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected synchronized Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {

        Class<?> c = findLoadedClass(className);
        if (c != null) {
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
        try {
            c = findClass(className);
        } catch (ClassNotFoundException ignore) { // NOPMD
        }
        if (c == null) {
            c = getParent().loadClass(className);
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }

    @Override
    public URL getResource(String name) {

        URL url = findResource(name);
        if (url == null) {
            if (getParent() == null) {
                if (getSystemClassLoader() != null) {
                    url = getSystemClassLoader().getResource(name);
                }
            } else {
                url = getParent().getResource(name);
            }
        }
        return url;
    }

}
