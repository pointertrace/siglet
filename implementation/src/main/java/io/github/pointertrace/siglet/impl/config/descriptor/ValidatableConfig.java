package io.github.pointertrace.siglet.impl.config.descriptor;

public interface ValidatableConfig<T extends ConfigurableDescriptor> {

   void validate(T descriptor);
}
