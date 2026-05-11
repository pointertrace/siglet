package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.StringValue;

public abstract class ConfigurableDescriptor extends BaseDescriptor {

    private StringValue type;

    private Object config;

    public StringValue getType() {
        return type;
    }

    protected void setType(StringValue type) {
        this.type = type;
    }

    public Object getConfig() {
        return config;
    }

    protected void setConfig(Object config) {
        this.config = config;
    }



    @Override
    public void validate() {
        if (config instanceof ValidatableConfig<?> validatableConfig) {
            validateWith(validatableConfig);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ConfigurableDescriptor> void validateWith(ValidatableConfig<T> validatableConfig) {
        validatableConfig.validate((T) this);
    }
}
