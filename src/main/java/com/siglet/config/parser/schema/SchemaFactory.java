package com.siglet.config.parser.schema;

import com.siglet.config.item.Item;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SchemaFactory {

    private SchemaFactory() {

    }

    public static NodeChecker text() {
        return new TextChecker();
    }

    public static NodeChecker alternative(NodeChecker ... alternatives) {
        return new AlternativeChecker(alternatives);
    }

    public static InetSocketAddressChecker inetSocketAddress() {
        return new InetSocketAddressChecker();
    }

    public static IntChecker numberInt(NodeChecker... additionalCheckers) {
        return new IntChecker(additionalCheckers);
    }

    public static LongChecker numberLong(NodeChecker... additionalCheckers) {
        return new LongChecker(additionalCheckers);
    }

    public static NodeChecker anyNumberChecker(NodeChecker ... aditionalCheckers) {
        return new AnyNumberChecker(aditionalCheckers);
    }

    public static NodeChecker text(NodeChecker... additionalCheckers) {
        return new TextChecker(additionalCheckers);
    }

    public static NodeChecker intRange(Integer lowInclusive, Integer highInclusive) {
        return new IntRangeChecker(lowInclusive, highInclusive);
    }

    public static <T, E> PropertyChecker property(BiConsumer<T, E> valueSetter, String propertyName, boolean required, NodeChecker... propertyChecks) {
        return new PropertyChecker(valueSetter, propertyName, required, propertyChecks);
    }

    public static AlternativePropertyChecker alternativePropertyChecker( String propertyName, boolean required, AbstractPropertyChecker ... abstractPropertyCheckers) {
        return new AlternativePropertyChecker( propertyName, required,abstractPropertyCheckers);
    }

    public static AlternativePropertyChecker alternativeRequiredProperty( String propertyName, AbstractPropertyChecker ... abstractPropertyCheckers) {
        return new AlternativePropertyChecker( propertyName, true,abstractPropertyCheckers);
    }

    public static DynamicPropertyChecker dynamicProperty(String propertyName, boolean required, DynamicCheckerDiscriminator discriminator) {
        return new DynamicPropertyChecker( propertyName, required,discriminator);
    }

    public static <T, E> PropertyChecker requiredProperty(BiConsumer<T, E> valueSetter, String propertyName, NodeChecker... propertyChecks) {
        return property(valueSetter, propertyName, true, propertyChecks);
    }

    public static <T, E> PropertyChecker optionalProperty(BiConsumer<T, E> valueSetter, String propertyName, NodeChecker... propertyChecks) {
        return property(valueSetter, propertyName, false, propertyChecks);
    }
    public static DynamicPropertyChecker requiredDynamicProperty(String propertyName, DynamicCheckerDiscriminator discriminator) {
        return dynamicProperty( propertyName, true, discriminator);
    }

    public static <T extends Item> NodeChecker object(Supplier<T> valueCreator, boolean strict, AbstractPropertyChecker... propertiesChecks) {
        return new ObjectChecker(valueCreator, strict, propertiesChecks);
    }

    public static <T extends Item> NodeChecker strictObject(Supplier<T> valueCreator, AbstractPropertyChecker... propertiesChecks) {
        return object(valueCreator, true, propertiesChecks);
    }

    public static NodeChecker array(NodeChecker... checks) {
        return new ArrayChecker(checks);
    }

}
