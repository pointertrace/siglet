package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.impl.config.descriptor.ConfigurableDescriptor;
import io.github.pointertrace.siglet.parser.Location;
import io.github.pointertrace.siglet.parser.Schema;
import io.github.pointertrace.siglet.parser.SchemaBuilder;
import io.github.pointertrace.siglet.parser.StringValue;
import io.github.pointertrace.siglet.parser.impl.schema.PropertySwitchSchema;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.stringValueObject;

public class ComponentTypeRegistry<T extends ComponentType<?, ?>> {

    private final Map<String, T> definitions = new HashMap<>();

    public Set<String> getTypes() {
        return Collections.unmodifiableSet(definitions.keySet());
    }

    public void register(T component) {
        definitions.put(component.getType(), component);
    }

    public void registerAll(Collection<? extends T> components) {
        definitions.putAll(components.stream().collect(Collectors.toMap(ComponentType::getType, Function.identity())));
    }
    public T get(String type) {
        return definitions.get(type);
    }

    public <V extends ConfigurableDescriptor> Schema.Builder<?, ?> getPropertySwitchSchema(BiConsumer<V, StringValue> nameSetter,
                                                                                           BiConsumer<V, StringValue> typeSetter) {

        @SuppressWarnings("unchecked")
        Schema.Builder<?, ?> switchCase = definitions.entrySet().stream()
                .map(entry -> {
                    Optional<Schema.Builder<?, ?>> schema = (Optional<Schema.Builder<?, ?>>) (Optional<?>) entry.getValue().getConfigurationFactory().createConfigSchema();
                    return schema
                            .map(s -> SchemaBuilder.switchCase(
                                    entry.getKey(),
                                    stringValueObject(),
                                    createNameAndTypeSetter(nameSetter, typeSetter, entry.getKey()),
                                    SchemaBuilder.property("config",fromSetter(ConfigurableDescriptor.class,"setConfig", Object.class), s)
                            ))
                            .orElseGet(() -> SchemaBuilder.switchCase(entry.getKey(), stringValueObject(), createNameAndTypeSetter(nameSetter, typeSetter, entry.getKey())));
                })
                .reduce(SchemaBuilder.propertySwitch(), PropertySwitchSchema.UnboundBuilder::addCase, (a, ignored) -> a);

        return switchCase;
    }

    protected static <T extends ConfigurableDescriptor> BiConsumer<T, StringValue> createNameAndTypeSetter(
            BiConsumer<T, StringValue> nameSetter, BiConsumer<T, StringValue> typeSetter, String type) {
        return (descriptor, name) -> {
            nameSetter.accept(descriptor, name);
            StringValue typeStringValue = new StringValue(type);
            if (name.getLocation() != null) {
                // YAML shape is `<type>: <name>`, so type starts before name by `<type>.length + 2` chars (`: `).
                int typeColumn = Math.max(1, name.getLocation().getColumn() - type.length() - 2);
                Location typeLocation = Location.of(name.getLocation().getLine(), typeColumn);
                typeStringValue.setLocation(typeLocation);
            }
            typeSetter.accept(descriptor, typeStringValue);
        };
    }

    public static <T> BiConsumer<T, Object> fromSetter(
            Class<? super T> targetClass,
            String setterName,
            Class<?> valueType) {

        try {
            MethodHandles.Lookup lookup =
                    MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());

            MethodHandle setter = lookup.findVirtual(
                    targetClass,
                    setterName,
                    MethodType.methodType(void.class, valueType)
            );

            return (target, value) -> {
                try {
                    setter.invoke(target, value);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };

        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Setter inválido", e);
        }
    }
}
