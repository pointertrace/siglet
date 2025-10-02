package io.github.pointertrace.siglet.impl.config.siglet;

import java.io.File;

public interface BundleLoader {

    SigletBundle load(File jarfile);

}
