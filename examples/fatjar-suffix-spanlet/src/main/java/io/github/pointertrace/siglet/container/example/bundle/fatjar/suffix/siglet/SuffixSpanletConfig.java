package io.github.pointertrace.siglet.container.example.bundle.fatjar.suffix.siglet;

/**
 * Configuration class for the {@link SuffixSpanlet}. Provides the necessary configuration
 * parameters to control the behavior of the suffix processing in span signals.
 *
 * This class holds the suffix string that is appended to a span's name during processing.
 * The specific configuration provided via this class influences the behavior of the
 * {@link SuffixSpanlet#span} method.
 *
 * Primary uses of this class include:
 * - Storing the suffix string needed for span name modification.
 * - Providing getter and setter methods to access or modify the suffix string.
 *
 * This configuration is expected to be passed via the {@link io.github.pointertrace.siglet.api.Context}
 * during the processing of a span.
 */
public class SuffixSpanletConfig {

    /**
     * Suffix to be appended to the span name.
     */
    private String suffix;

    /**
     * Returns the suffix to be appended to the span name.
     *
     * @return the suffix, or {@code null} if not configured
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Sets the suffix to be appended to the span name.
     *
     * @param suffix the suffix to use
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
