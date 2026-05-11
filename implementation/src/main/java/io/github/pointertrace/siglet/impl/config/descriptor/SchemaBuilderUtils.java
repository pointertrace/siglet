package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.Schema;
import io.github.pointertrace.siglet.parser.StringValue;
import io.github.pointertrace.siglet.parser.ValueTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class SchemaBuilderUtils {

    private SchemaBuilderUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Schema.Builder<?, ?> destinationSchemaBuilder(String propertyName, BiConsumer<T, List<StringValue>> propertySetter) {
        return property(propertyName, propertySetter,(Schema.Builder<?, List<StringValue>>) (Schema.Builder<?, ?>)
                choice(
                        array(ArrayList::new, arrayItem(List::add, stringValueObject())),
                        stringValueObject().transform(new StringValueToArrayListStringValueTransform())
                )).customErrorMessage("#location " + destinationErrorMessage(propertyName))
                .onlyCustomMessagesUpToHere();
    }

    private static String destinationErrorMessage(String propertyName) {
        return "Destination (" + propertyName + " property) must be a string or a list of strings";
    }

    private static class StringValueToArrayListStringValueTransform implements ValueTransform<StringValue, List<StringValue>> {

        @Override
        public List<StringValue> transform(StringValue value) {
            return List.of(value);
        }
    }
}
