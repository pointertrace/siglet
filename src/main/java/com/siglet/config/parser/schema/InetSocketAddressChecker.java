package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueNode;
import com.siglet.config.parser.node.ValueTransformer;

import java.net.InetSocketAddress;

public class InetSocketAddressChecker extends NodeChecker {

    private final ValueTransformer inetSocketAddressTransformer = new InetSocketAddressValueTransformer();

    public void check(Node node) throws SchemaValidationError {
        if (node instanceof ValueNode.TextNode textNode) {
            String validationError = getValidationErrorReason((String) textNode.getValue());
            if (validationError != null) {
                throw new SingleSchemaValidationError(node.getLocation(), validationError);
            }
        } else {
            throw new SingleSchemaValidationError(node.getLocation(), "is not a valid address <host>:<port>!");
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
