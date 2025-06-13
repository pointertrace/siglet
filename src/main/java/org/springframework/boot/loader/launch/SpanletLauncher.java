package org.springframework.boot.loader.launch;

public class SpanletLauncher extends JarLauncher {
    protected SpanletLauncher(Archive archive) throws Exception {
        super(archive);
    }

    public ClassLoader getClassLoader() throws Exception {
        return createClassLoader(((JarFileArchive) getArchive()).getClassPathUrls((x)-> true));
    }
}
