package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.ValueTransformer;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SchemaFactory {

    private SchemaFactory() {

    }

    public static NodeChecker text() {
        return new TextChecker();
    }

    public static NodeChecker alternative(NodeChecker... alternatives) {
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

    public static NodeChecker anyNumberChecker(NodeChecker... additionalCheckers) {
        return new AnyNumberChecker(additionalCheckers);
    }

    public static NodeChecker text(ValueTransformer valueTransformer, NodeChecker... additionalCheckers) {
        return new TextChecker(valueTransformer, additionalCheckers);
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

    public static <T, E, R extends Located> PropertyChecker property(BiConsumer<T, E> valueSetter, BiConsumer<R, Location> locationSetter, String propertyName, boolean required, NodeChecker... propertyChecks) {
        return new PropertyChecker(valueSetter, locationSetter, propertyName, required, propertyChecks);
    }

    public static AlternativePropertyChecker alternativePropertyChecker(String propertyName, boolean required, AbstractPropertyChecker... abstractPropertyCheckers) {
        return new AlternativePropertyChecker(propertyName, required, abstractPropertyCheckers);
    }

    public static AlternativePropertyChecker alternativeRequiredProperty(String propertyName, AbstractPropertyChecker... abstractPropertyCheckers) {
        return new AlternativePropertyChecker(propertyName, true, abstractPropertyCheckers);
    }

    public static DynamicPropertyChecker dynamicProperty(String propertyName, boolean required, DynamicCheckerDiscriminator discriminator) {
        return new DynamicPropertyChecker(propertyName, required, discriminator);
    }

    public static <R extends Located> DynamicPropertyChecker dynamicProperty(String propertyName, BiConsumer<R, Location> locationSetter, boolean required, DynamicCheckerDiscriminator discriminator) {
        return new DynamicPropertyChecker(propertyName, locationSetter, required, discriminator);
    }

    public static <T, E> PropertyChecker requiredProperty(BiConsumer<T, E> valueSetter, String propertyName, NodeChecker... propertyChecks) {
        return property(valueSetter, propertyName, true, propertyChecks);
    }

    public static <T, E, R extends Located> PropertyChecker requiredProperty(BiConsumer<T, E> valueSetter, BiConsumer<R, Location> locationSetter, String propertyName, NodeChecker... propertyChecks) {
        return property(valueSetter, locationSetter, propertyName, true, propertyChecks);
    }

    public static <T, E> PropertyChecker optionalProperty(BiConsumer<T, E> valueSetter, String propertyName, NodeChecker... propertyChecks) {
        return property(valueSetter, propertyName, false, propertyChecks);
    }

    public static <T, E, R extends Located> PropertyChecker optionalProperty(BiConsumer<T, E> valueSetter, BiConsumer<R, Location> locationSetter, String propertyName, NodeChecker... propertyChecks) {
        return property(valueSetter, locationSetter, propertyName, false, propertyChecks);
    }

    public static DynamicPropertyChecker requiredDynamicProperty(String propertyName, DynamicCheckerDiscriminator discriminator) {
        return dynamicProperty(propertyName, true, discriminator);
    }

    public static <R extends Located> DynamicPropertyChecker requiredDynamicProperty(String propertyName, BiConsumer<R, Location> locationSetter, DynamicCheckerDiscriminator discriminator) {
        return dynamicProperty(propertyName, locationSetter, true, discriminator);
    }

    public static <TK, EK, RK extends Located, TV, EV, RV extends Located> DynamicKeyPropertyChecker dynamicKeyProperty(
            BiConsumer<TK, EK> keySetter, BiConsumer<RK, Location> keyLocationSetter,
            BiConsumer<TV, EV> valueSetter, BiConsumer<RV, Location> valueLocationSetter,
            Set<String> propertyKeyDomain, boolean required, NodeChecker propertyValueCheck) {
        return new DynamicKeyPropertyChecker(keySetter, keyLocationSetter, valueSetter, valueLocationSetter,
                propertyKeyDomain, required, propertyValueCheck);
    }

    public static <TK, EK, RK extends Located, TV, EV, RV extends Located> DynamicKeyPropertyChecker requiredDynamicKeyProperty(
            BiConsumer<TK, EK> keySetter, BiConsumer<RK, Location> keyLocationSetter,
            BiConsumer<TV, EV> valueSetter, BiConsumer<RV, Location> valueLocationSetter,
            Set<String> propertyKeyDomain, NodeChecker propertyValueCheck) {
        return new DynamicKeyPropertyChecker(keySetter, keyLocationSetter, valueSetter, valueLocationSetter,
                propertyKeyDomain, true, propertyValueCheck);
    }

    public static <TK, EK, RK extends Located, TV, EV, RV extends Located> DynamicKeyPropertyChecker optionalDynamicKeyProperty(
            BiConsumer<TK, EK> keySetter, BiConsumer<RK, Location> keyLocationSetter,
            BiConsumer<TV, EV> valueSetter, BiConsumer<RV, Location> valueLocationSetter,
            Set<String> propertyKeyDomain, NodeChecker propertyValueCheck) {
        return new DynamicKeyPropertyChecker(keySetter, keyLocationSetter, valueSetter, valueLocationSetter,
                propertyKeyDomain, false, propertyValueCheck);
    }
    public static NodeChecker object(Supplier<?> valueCreator, boolean strict, AbstractPropertyChecker... propertiesChecks) {
        return new ObjectChecker(valueCreator, strict, propertiesChecks);
    }

    public static NodeChecker strictObject(Supplier<?> valueCreator, AbstractPropertyChecker... propertiesChecks) {
        return object(valueCreator, true, propertiesChecks);
    }

    public static NodeChecker array(NodeChecker... checks) {
        return new ArrayChecker(checks);
    }

    public static <VC, VST, VSE, LSR, IC, AIT, AIE> NodeChecker array(Supplier<VC> valueCreator, BiConsumer<VST, VSE> valueSetter,
                                                                          BiConsumer<LSR, Location> locationSetter,
                                                                          Supplier<IC> arrayItemCreator,
                                                                          BiConsumer<AIT, AIE> arrayItemValueSetter,
                                                                          NodeChecker... checks) {
        return new ArrayChecker(valueCreator, valueSetter, locationSetter, arrayItemCreator, arrayItemValueSetter,
                checks);
    }
}
