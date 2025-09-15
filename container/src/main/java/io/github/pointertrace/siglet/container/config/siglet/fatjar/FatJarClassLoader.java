package io.github.pointertrace.siglet.container.config.siglet.fatjar;

import io.github.pointertrace.siglet.api.SigletError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class FatJarClassLoader extends ClassLoader {

    private final JarFile jar;

    private final Map<String, Set<ResourceLocation>> packageIndex = new HashMap<>();

    public FatJarClassLoader(JarFile jar, ClassLoader parent) {
        super(parent);
        this.jar = jar;
        buildIndex();
    }



    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);

        if (c != null) {
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
        try {
            c = findClass(name);
        } catch (ClassNotFoundException e) {
        }
        if (c == null) {
            c = getParent().loadClass(name);
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;

    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/') + ".class";
        String pkg = getPackageName(path);
        Set<ResourceLocation> locations = packageIndex.get(pkg);

        if (locations != null) {
            for (ResourceLocation loc : locations) {
                try (InputStream is = openResourceStream(loc, path)) {
                    if (is != null) {
                        byte[] bytes = readAllBytes(is);
                        return defineClass(name, bytes, 0, bytes.length);
                    }
                } catch (IOException ignored) {
                }
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public URL getResource(String name) {
        URL resource = findResource(name);
        if (resource != null) {
            return resource;
        }
        return super.getResource(name);
    }

    @Override
    protected URL findResource(String name) {
        String pkg = getPackageName(name);
        Set<ResourceLocation> locations = packageIndex.get(pkg);

        if (locations != null) {
            for (ResourceLocation loc : locations) {
                try (InputStream is = openResourceStream(loc, name)) {
                    if (is != null) {
                        return new URL("jar:file:" + jar.getName() + "!/" +
                                       (loc.jarEntry != null ? loc.jarEntry.getName() + "!/" : "") + name);
                    }
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream is = findResourceAsStream(name);
        if (is != null) {
            return is;
        }
        return super.getResourceAsStream(name);
    }


    private InputStream findResourceAsStream(String name) {
        String pkg = getPackageName(name);
        Set<ResourceLocation> locations = packageIndex.get(pkg);

        if (locations != null) {
            for (ResourceLocation loc : locations) {
                try {
                    InputStream is = openResourceStream(loc, name);
                    if (is != null) return is;
                } catch (IOException ignored) {
                }
            }
        }
        return null;

    }

    private InputStream openResourceStream(ResourceLocation loc, String name) throws IOException {
        if (loc.nestedJarName == null) {
            JarEntry entry = jar.getJarEntry(name);
            return entry != null ? jar.getInputStream(entry) : null;
        } else {
            try (JarInputStream jis = new JarInputStream(jar.getInputStream(loc.jarEntry))) {
                JarEntry e;
                while ((e = jis.getNextJarEntry()) != null) {
                    if (e.getName().equals(name)) {
                        return new ByteArrayInputStream(readAllBytes(jis));
                    }
                }
            }
            return null;
        }
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] tmp = new byte[4096];
        int read;
        while ((read = is.read(tmp)) != -1) {
            buffer.write(tmp, 0, read);
        }
        return buffer.toByteArray();
    }


    private void buildIndex() {

        Enumeration<JarEntry> entries = jar.entries();
        List<JarEntry> nestedJars = new ArrayList<>();

        addToIndex("", new ResourceLocation(jar, null, null));
        try {
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith("lib/") && entry.getName().endsWith(".jar")) {
                    nestedJars.add(entry);
                } else {
                    String pkg = getPackageName(entry.getName());
                    addToIndex(pkg, new ResourceLocation(jar, null, null));
                }

                for (JarEntry nestedJarEntry : nestedJars) {
                    try (JarInputStream jis = new JarInputStream(jar.getInputStream(nestedJarEntry))) {
                        JarEntry nestedEntry;
                        while ((nestedEntry = jis.getNextJarEntry()) != null) {
                            if (!nestedEntry.isDirectory()) {
                                String pkg = getPackageName(nestedEntry.getName());
                                addToIndex(pkg, new ResourceLocation(jar, nestedJarEntry, nestedJarEntry.getName()));
                            }
                        }
                    }
                    addToIndex("", new ResourceLocation(jar, nestedJarEntry, nestedJarEntry.getName()));
                }
            }
        } catch (IOException e) {
            throw new SigletError("Error reading jar file", e);
        }
    }

    private void addToIndex(String pkg, ResourceLocation location) {
        packageIndex.computeIfAbsent(pkg, k -> new HashSet<>()).add(location);
    }

    private String getPackageName(String path) {
        int idx = path.lastIndexOf('/');
        return (idx != -1) ? path.substring(0, idx + 1) : "";
    }

    @Override
    public String toString() {
        return jar.getName();
    }

    public record ResourceLocation(JarFile jarFile, JarEntry jarEntry, String nestedJarName) {
    }

}
