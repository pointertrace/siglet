package io.github.pointertrace.siglet.container.config.siglet.parser;

import io.github.pointertrace.siglet.container.SigletError;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class SigletClassLoader extends ClassLoader {

    private final JarFile rootJar;

    private final Map<String, Set<ResourceLocation>> packageIndex = new HashMap<>();

    public SigletClassLoader(File uberJarFile, ClassLoader parent) throws IOException {
        super(parent);
        this.rootJar = new JarFile(uberJarFile);
        buildIndex();
    }

    public InputStream getSigletConfigInputStream() {

        InputStream is = findResourceAsStream("META-INF/siglet-config.yaml");
        if (is == null) {
            throw new SigletError("Could not find META-INF/siglet-config.yaml in the jar file " + rootJar.getName());
        }
        return is;
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
                        return new URL("jar:file:" + rootJar.getName() + "!/" +
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
            JarEntry entry = rootJar.getJarEntry(name);
            return entry != null ? rootJar.getInputStream(entry) : null;
        } else {
            try (JarInputStream jis = new JarInputStream(rootJar.getInputStream(loc.jarEntry))) {
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


    private void buildIndex() throws IOException {
        Enumeration<JarEntry> entries = rootJar.entries();
        List<JarEntry> nestedJars = new ArrayList<>();

        addToIndex("", new ResourceLocation(rootJar, null, null));
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith("lib/") && entry.getName().endsWith(".jar")) {
                nestedJars.add(entry);
            } else {
                String pkg = getPackageName(entry.getName());
                addToIndex(pkg, new ResourceLocation(rootJar, null, null));
            }

            for (JarEntry nestedJarEntry : nestedJars) {
                try (JarInputStream jis = new JarInputStream(rootJar.getInputStream(nestedJarEntry))) {
                    JarEntry nestedEntry;
                    while ((nestedEntry = jis.getNextJarEntry()) != null) {
                        if (!nestedEntry.isDirectory()) {
                            String pkg = getPackageName(nestedEntry.getName());
                            addToIndex(pkg, new ResourceLocation(rootJar, nestedJarEntry, nestedJarEntry.getName()));
                        }
                    }
                }
                addToIndex("", new ResourceLocation(rootJar, nestedJarEntry, nestedJarEntry.getName()));
            }
        }
    }

    private void addToIndex(String pkg, ResourceLocation location) {
        packageIndex.computeIfAbsent(pkg, k -> new HashSet<>()).add(location);
    }

    private String getPackageName(String path) {
        int idx = path.lastIndexOf('/');
        return (idx != -1) ? path.substring(0, idx + 1) : "";
    }

    public record ResourceLocation(JarFile jarFile, JarEntry jarEntry, String nestedJarName) {
    }

}
