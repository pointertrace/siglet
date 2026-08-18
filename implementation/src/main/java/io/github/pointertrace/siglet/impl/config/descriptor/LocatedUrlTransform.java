package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.parser.Location;
import io.github.pointertrace.siglet.parser.ValueTransform;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class LocatedUrlTransform implements ValueTransform<String, LocatedUrl>{


    @Override
    public LocatedUrl transform(String value, Location location) {
        try {
            return new LocatedUrl(new URI(value).toURL(), location);
        } catch (Exception e) {
            throw new SigletError(String.format("%s must be a valid URL format",location.print()));

        }

    }
}
