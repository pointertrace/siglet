package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.ValueTransformer;
import io.github.pointertrace.siglet.parser.node.ValueNode;

import java.net.InetSocketAddress;

public class InetSocketAddressChecker extends BaseNodeChecker {

    private final ValueTransformer inetSocketAddressTransformer = new InetSocketAddressValueTransformer();

    public void check(Node node) throws SchemaValidationError {
        if (node instanceof ValueNode.TextNode) {
            String validationError = getValidationErrorReason((String) node.getValue());
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
