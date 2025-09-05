package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.Utils;
import io.github.pointertrace.siglet.parser.located.Location;

import java.util.Collections;
import java.util.List;

public class MultipleSchemaValidationError extends SchemaValidationError {

    private final List<? extends SchemaValidationError> validationExceptions;

    public MultipleSchemaValidationError(Location location, String localMessage, List<? extends SchemaValidationError> validationExceptions) {
        super(location, localMessage);
        this.validationExceptions = validationExceptions;
    }


    public List<? extends SchemaValidationError> getValidationExceptions() {
        return Collections.unmodifiableList(validationExceptions);
    }

    @Override
    public String explain(int level) {
        StringBuilder msg = new StringBuilder(String.format("%s%s %s because:", Utils.repeat(PREFIX,level),
                getLocation().describe(), getLocalMessage()));
        for (SchemaValidationError causeExc : getValidationExceptions()) {
            msg.append('\n');
            msg.append(causeExc.explain(level + 1));
        }
        return msg.toString();
    }
}
