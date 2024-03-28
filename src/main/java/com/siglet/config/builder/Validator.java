package com.siglet.config.builder;

import com.siglet.SigletError;

public interface Validator {

    void validate(GlobalConfigBuilder globalConfigBuilder) throws SigletError;
}
