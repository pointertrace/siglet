package io.github.pointertrace.siglet.parser.schema;


import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.Utils;
import io.github.pointertrace.siglet.parser.located.Location;

public class SingleSchemaValidationError extends SchemaValidationError {

    public SingleSchemaValidationError(Location location, String localMessage) {
        super(location, localMessage);
    }

    public SingleSchemaValidationError(Location location, String message, SchemaValidationError cause) {
        super(location, message, cause);
    }

    @Override
    public String explain(int level) {
        if (getCause() != null) {
            return String.format("%s%s %s because:%n%s", Utils.repeat(PREFIX,level), getLocation().describe(),
                    getLocalMessage(),
                    getCause().explain(level + 1));
        } else {
            return String.format("%s%s %s", Utils.repeat(PREFIX,level), getLocation().describe(),
                    getLocalMessage());
        }
    }

}
