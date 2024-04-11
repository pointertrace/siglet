package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;
import com.siglet.config.parser.node.ValueTransformer;

import java.net.InetSocketAddress;

public class InetSocketAddressChecker implements NodeChecker {

    private ValueTransformer inetSocketAddressTransformer = new InetSocketAddressValueTransformer();

    public void check(ConfigNode node) throws SchemaValidationError {
        if (node instanceof ValueConfigNode.Text textNode) {
            String validationError = getValidationErrorReason((String) textNode.getValue());
            if (validationError != null) {
                throw new SingleSchemaValidationError(validationError, node.getLocation());
            }
        } else {
            throw new SingleSchemaValidationError("is not a valid address <host>:<port>!", node.getLocation());
        }
    }

    @Override
    public String getName() {
        return "inetSocketAddress";
    }

    @Override
    public ValueTransformer getValueTransformer() {
        return inetSocketAddressTransformer;
    }

    private String getValidationErrorReason(String value) {
        String[] parts = value.split(":");
        if (parts.length != 2) {
            return "is not in format <host>:<port>";
        }
        try {
            InetSocketAddress.createUnresolved(parts[0], Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            return parts[1] + " is not a valid port number";
        } catch (IllegalArgumentException e) {
            return value + " is not a valid address because " + e.getMessage();
        }
        return null;
    }
}
