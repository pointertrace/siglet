package io.github.pointertrace.siglet.container.config.siglet;

import java.io.File;

public interface BundleLoader {

    SigletBundle load(File jarfile);

}
